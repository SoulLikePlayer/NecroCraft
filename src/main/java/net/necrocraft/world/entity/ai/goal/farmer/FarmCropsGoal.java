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

public class FarmCropsGoal extends Goal {

    private static final int RESCAN_INTERVAL_TICKS = 20;
    private static final int VERTICAL_RADIUS = 2;
    private static final double INTERACT_RANGE_SQ = 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;
    private final int horizontalRadius;

    private @Nullable BlockPos targetCropPos;
    private int rescanCooldown;

    public FarmCropsGoal(AbstractMinion minion, double speedModifier, int horizontalRadius) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.horizontalRadius = horizontalRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

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

    @Override
    public boolean canContinueToUse() {
        if (this.targetCropPos == null || !this.minion.isSedentary()) {
            return false;
        }
        BlockState state = this.minion.level().getBlockState(this.targetCropPos);
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }

    @Override
    public void start() {
        moveTowardTarget();
    }

    @Override
    public void stop() {
        this.targetCropPos = null;
        this.minion.getNavigation().stop();
    }

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