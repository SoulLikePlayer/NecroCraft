package net.necrocraft.world.entity.ai.goal.farmer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;

/**
 * Makes a sedentary farmer minion seek out the nearest empty (unplanted)
 * farmland block within range, walk to it, and plant a seed from its own
 * inventory on top of it.
 */
public class PlantSeedsGoal extends Goal {

    private static final int RESCAN_INTERVAL_TICKS = 20;
    private static final int VERTICAL_RADIUS = 2;
    private static final double INTERACT_RANGE_SQ = 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;
    private final int horizontalRadius;

    private @Nullable BlockPos targetFarmlandPos;
    private int rescanCooldown;

    /**
     * @param minion           the minion that should plant seeds
     * @param speedModifier    movement speed multiplier applied while walking to farmland
     * @param horizontalRadius horizontal search radius (in blocks) for empty farmland
     */
    public PlantSeedsGoal(AbstractMinion minion, double speedModifier, int horizontalRadius) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.horizontalRadius = horizontalRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * Determines whether the goal should start: the minion must be sedentary,
     * the rescan cooldown must have elapsed, the minion must carry at least
     * one seed, and empty farmland must be found within range.
     *
     * @return {@code true} if a target farmland tile was found and the goal should start
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

        if (!hasAnySeed()) {
            return false;
        }

        this.targetFarmlandPos = findNearestEmptyFarmland();
        return this.targetFarmlandPos != null;
    }

    /**
     * @return {@code true} if the minion is still sedentary, the target
     *         farmland is still empty, and the minion still carries a seed
     */
    @Override
    public boolean canContinueToUse() {
        if (this.targetFarmlandPos == null || !this.minion.isSedentary()) {
            return false;
        }
        return isEmptyFarmland(this.minion.level(), this.targetFarmlandPos) && hasAnySeed();
    }

    /** Starts moving the minion toward the target farmland. */
    @Override
    public void start() {
        moveTowardTarget();
    }

    /** Clears the target farmland and stops the minion's navigation. */
    @Override
    public void stop() {
        this.targetFarmlandPos = null;
        this.minion.getNavigation().stop();
    }

    /**
     * Each tick, looks at the target farmland and either walks toward it (if
     * out of interaction range) or plants a seed on it (once in range).
     */
    @Override
    public void tick() {
        if (this.targetFarmlandPos == null) {
            return;
        }

        this.minion.getLookControl().setLookAt(
                this.targetFarmlandPos.getX() + 0.5D,
                this.targetFarmlandPos.getY() + 0.5D,
                this.targetFarmlandPos.getZ() + 0.5D
        );

        if (this.minion.distanceToSqr(Vec3.atCenterOf(this.targetFarmlandPos)) <= INTERACT_RANGE_SQ) {
            this.minion.getNavigation().stop();
            if (this.minion.level() instanceof ServerLevel serverLevel) {
                plantSeed(serverLevel, this.targetFarmlandPos);
            }
            this.targetFarmlandPos = null;
        } else if (this.minion.getNavigation().isDone()) {
            moveTowardTarget();
        }
    }

    /** Orders the minion's navigation to move to the currently targeted farmland. */
    private void moveTowardTarget() {
        if (this.targetFarmlandPos != null) {
            this.minion.getNavigation().moveTo(
                    this.targetFarmlandPos.getX() + 0.5D,
                    this.targetFarmlandPos.getY(),
                    this.targetFarmlandPos.getZ() + 0.5D,
                    this.speedModifier
            );
        }
    }

    /**
     * Plants the first seed the minion carries (in crop-to-seed map iteration
     * order) above the given farmland block, consuming one seed from the
     * minion's inventory.
     *
     * @param level       the server level containing the farmland
     * @param farmlandPos the position of the empty farmland block
     */
    private void plantSeed(ServerLevel level, BlockPos farmlandPos) {
        if (!isEmptyFarmland(level, farmlandPos)) {
            return;
        }

        for (Map.Entry<Block, Item> entry : FarmingUtil.cropToSeedMap().entrySet()) {
            if (this.minion.consumeItem(entry.getValue(), 1)) {
                level.setBlockAndUpdate(farmlandPos.above(), entry.getKey().defaultBlockState());
                return;
            }
        }
    }

    /**
     * @return {@code true} if the minion's inventory contains at least one
     *         of any known crop seed
     */
    private boolean hasAnySeed() {
        for (Item seed : FarmingUtil.cropToSeedMap().values()) {
            if (this.minion.hasItem(seed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param level the level to read block states from
     * @param pos   the farmland position to check
     * @return {@code true} if {@code pos} is farmland with nothing planted above it
     */
    private boolean isEmptyFarmland(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != Blocks.FARMLAND) {
            return false;
        }
        return level.getBlockState(pos.above()).isAir();
    }

    /**
     * Scans a box around the minion (see {@link #horizontalRadius} and
     * {@link #VERTICAL_RADIUS}) for the closest empty farmland block.
     *
     * @return the nearest empty farmland position, or {@code null} if none was found
     */
    private @Nullable BlockPos findNearestEmptyFarmland() {
        Level level = this.minion.level();
        BlockPos origin = this.minion.blockPosition();
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (int dx = -this.horizontalRadius; dx <= this.horizontalRadius; dx++) {
            for (int dz = -this.horizontalRadius; dz <= this.horizontalRadius; dz++) {
                for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (isEmptyFarmland(level, pos)) {
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