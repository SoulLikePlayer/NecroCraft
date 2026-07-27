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

public class PlantSeedsGoal extends Goal {

    private static final int RESCAN_INTERVAL_TICKS = 20;
    private static final int VERTICAL_RADIUS = 2;
    private static final double INTERACT_RANGE_SQ = 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;
    private final int horizontalRadius;

    private @Nullable BlockPos targetFarmlandPos;
    private int rescanCooldown;

    public PlantSeedsGoal(AbstractMinion minion, double speedModifier, int horizontalRadius) {
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

        if (!hasAnySeed()) {
            return false;
        }

        this.targetFarmlandPos = findNearestEmptyFarmland();
        return this.targetFarmlandPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetFarmlandPos == null || !this.minion.isSedentary()) {
            return false;
        }
        return isEmptyFarmland(this.minion.level(), this.targetFarmlandPos) && hasAnySeed();
    }

    @Override
    public void start() {
        moveTowardTarget();
    }

    @Override
    public void stop() {
        this.targetFarmlandPos = null;
        this.minion.getNavigation().stop();
    }

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

    private boolean hasAnySeed() {
        for (Item seed : FarmingUtil.cropToSeedMap().values()) {
            if (this.minion.hasItem(seed)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptyFarmland(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != Blocks.FARMLAND) {
            return false;
        }
        return level.getBlockState(pos.above()).isAir();
    }

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