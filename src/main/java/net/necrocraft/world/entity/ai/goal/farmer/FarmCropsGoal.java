package net.necrocraft.world.entity.ai.goal.farmer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Makes a "sedentary" farmer minion (see {@link AbstractMinion#isSedentary()})
 * seek out the nearest fully grown crop within range, walk to it, harvest it,
 * store the drops in its own inventory, and replant a seed from that
 * inventory if one is available.
 */
public class FarmCropsGoal extends Goal {

    private static final int RESCAN_INTERVAL_TICKS = 20;
    private static final int VERTICAL_RADIUS = 2;
    private static final double INTERACT_RANGE_SQ = 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;
    private final int horizontalRadius;

    private @Nullable BlockPos targetCropPos;
    private int rescanCooldown;

    /**
     * @param minion           the minion that should farm crops
     * @param speedModifier    movement speed multiplier applied while walking to a crop
     * @param horizontalRadius horizontal search radius (in blocks) for mature crops
     */
    public FarmCropsGoal(AbstractMinion minion, double speedModifier, int horizontalRadius) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.horizontalRadius = horizontalRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * Determines whether the goal should start: the minion must be sedentary,
     * the rescan cooldown must have elapsed, and a mature crop must be found
     * within range.
     *
     * @return {@code true} if a target crop was found and the goal should start
     */
    @Override
    public boolean canUse() {
        if (!this.minion.isSedentary()) {
            return false;
        }
        if (this.rescanCooldown > 0) {
            this.rescanCooldown--;
            return false;
        }
        this.rescanCooldown = RESCAN_INTERVAL_TICKS;
        this.targetCropPos = findNearestMatureCrop();
        return this.targetCropPos != null;
    }

    /**
     * @return {@code true} if the minion is still sedentary and the target
     *         position still holds a fully grown crop
     */
    @Override
    public boolean canContinueToUse() {
        if (this.targetCropPos == null || !this.minion.isSedentary()) {
            return false;
        }
        BlockState state = this.minion.level().getBlockState(this.targetCropPos);
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }

    /** Starts moving the minion toward the target crop. */
    @Override
    public void start() {
        moveTowardTarget();
    }

    /** Clears the target crop and stops the minion's navigation. */
    @Override
    public void stop() {
        this.targetCropPos = null;
        this.minion.getNavigation().stop();
    }

    /**
     * Each tick, looks at the target crop and either walks toward it (if out
     * of interaction range) or harvests and replants it (once in range).
     */
    @Override
    public void tick() {
        if (this.targetCropPos == null) {
            return;
        }

        this.minion.getLookControl().setLookAt(
                this.targetCropPos.getX() + 0.5D,
                this.targetCropPos.getY() + 0.5D,
                this.targetCropPos.getZ() + 0.5D
        );

        if (this.minion.distanceToSqr(Vec3.atCenterOf(this.targetCropPos)) <= INTERACT_RANGE_SQ) {
            this.minion.getNavigation().stop();
            if (this.minion.level() instanceof ServerLevel serverLevel) {
                harvestAndReplant(serverLevel, this.targetCropPos);
            }
            this.targetCropPos = null;
        } else if (this.minion.getNavigation().isDone()) {
            moveTowardTarget();
        }
    }

    /** Orders the minion's navigation to move to the currently targeted crop. */
    private void moveTowardTarget() {
        if (this.targetCropPos != null) {
            this.minion.getNavigation().moveTo(
                    this.targetCropPos.getX() + 0.5D,
                    this.targetCropPos.getY(),
                    this.targetCropPos.getZ() + 0.5D,
                    this.speedModifier
            );
        }
    }

    /**
     * Harvests the mature crop at {@code pos}, storing its drops in the
     * minion's inventory (dropping anything that doesn't fit on the ground),
     * removes the crop block, and replants a seed from the minion's
     * inventory if a matching one is available and consumed successfully.
     *
     * @param level the server level containing the crop
     * @param pos   the position of the mature crop
     */
    private void harvestAndReplant(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        for (ItemStack drop : Block.getDrops(state, level, pos, blockEntity, this.minion, ItemStack.EMPTY)) {
            ItemStack leftover = this.minion.storeItemStack(drop);
            if (!leftover.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), leftover);
            }
        }

        level.removeBlock(pos, false);

        Item seed = FarmingUtil.cropToSeedMap().get(crop);
        if (seed != null && this.minion.consumeItem(seed, 1)) {
            level.setBlockAndUpdate(pos, crop.defaultBlockState());
        }
    }

    /**
     * Scans a box around the minion (see {@link #horizontalRadius} and
     * {@link #VERTICAL_RADIUS}) for the closest fully grown crop.
     *
     * @return the nearest mature crop position, or {@code null} if none was found
     */
    private @Nullable BlockPos findNearestMatureCrop() {
        Level level = this.minion.level();
        BlockPos origin = this.minion.blockPosition();
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (int dx = -this.horizontalRadius; dx <= this.horizontalRadius; dx++) {
            for (int dz = -this.horizontalRadius; dz <= this.horizontalRadius; dz++) {
                for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                        double distSq = origin.distSqr(pos);
                        if (distSq < bestDistSq) {
                            bestDistSq = distSq;
                            best = pos.immutable();
                        }
                    }
                }
            }
        }

        return best;
    }
}