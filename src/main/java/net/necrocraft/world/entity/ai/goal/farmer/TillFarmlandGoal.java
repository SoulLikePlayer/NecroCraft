package net.necrocraft.world.entity.ai.goal.farmer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;

/**
 * Makes a minion carrying a hoe (equipped or in inventory) find nearby
 * tillable soil that is close to water, walk to it, till it into farmland
 * (damaging the hoe in the process), and immediately plant a seed on it if
 * one is available.
 */
public class TillFarmlandGoal extends Goal {

    private static final int RESCAN_INTERVAL_TICKS = 20;
    private static final int VERTICAL_RADIUS = 1;
    private static final int MAX_WATER_DISTANCE = 4;
    private static final double INTERACT_RANGE_SQ = 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;
    private final int horizontalRadius;

    private @Nullable BlockPos targetSoilPos;
    private int rescanCooldown;

    /**
     * @param minion           the minion that should till soil
     * @param speedModifier    movement speed multiplier applied while walking to the soil
     * @param horizontalRadius horizontal search radius (in blocks) for tillable soil
     */
    public TillFarmlandGoal(AbstractMinion minion, double speedModifier, int horizontalRadius) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.horizontalRadius = horizontalRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * Determines whether the goal should start: the rescan cooldown must have
     * elapsed, the minion must carry a hoe, and tillable soil near water must
     * be found within range.
     *
     * @return {@code true} if a target soil block was found and the goal should start
     */
    @Override
    public boolean canUse() {
        if (this.rescanCooldown > 0) {
            this.rescanCooldown--;
            return false;
        }
        this.rescanCooldown = RESCAN_INTERVAL_TICKS;

        if (!hasHoe()) {
            return false;
        }

        this.targetSoilPos = findNearestTillableSoil();
        return this.targetSoilPos != null;
    }

    /**
     * @return {@code true} if the target soil is still tillable and the
     *         minion still carries a hoe
     */
    @Override
    public boolean canContinueToUse() {
        if (this.targetSoilPos == null) {
            return false;
        }
        return isTillableSoil(this.minion.level(), this.targetSoilPos) && hasHoe();
    }

    /** Starts moving the minion toward the target soil. */
    @Override
    public void start() {
        moveTowardTarget();
    }

    /** Clears the target soil and stops the minion's navigation. */
    @Override
    public void stop() {
        this.targetSoilPos = null;
        this.minion.getNavigation().stop();
    }

    /**
     * Each tick, looks at the target soil and either walks toward it (if out
     * of interaction range) or tills and plants it (once in range).
     */
    @Override
    public void tick() {
        if (this.targetSoilPos == null) {
            return;
        }

        this.minion.getLookControl().setLookAt(
                this.targetSoilPos.getX() + 0.5D,
                this.targetSoilPos.getY() + 0.5D,
                this.targetSoilPos.getZ() + 0.5D
        );

        if (this.minion.distanceToSqr(Vec3.atCenterOf(this.targetSoilPos)) <= INTERACT_RANGE_SQ) {
            this.minion.getNavigation().stop();
            if (this.minion.level() instanceof ServerLevel serverLevel) {
                tillAndPlant(serverLevel, this.targetSoilPos);
            }
            this.targetSoilPos = null;
        } else if (this.minion.getNavigation().isDone()) {
            moveTowardTarget();
        }
    }

    /** Orders the minion's navigation to move to the currently targeted soil. */
    private void moveTowardTarget() {
        if (this.targetSoilPos != null) {
            this.minion.getNavigation().moveTo(
                    this.targetSoilPos.getX() + 0.5D,
                    this.targetSoilPos.getY(),
                    this.targetSoilPos.getZ() + 0.5D,
                    this.speedModifier
            );
        }
    }

    /**
     * Tills the soil at {@code pos} into farmland (damaging the minion's hoe),
     * plays the tilling sound, and plants the first available seed from the
     * minion's inventory on top of it, if any.
     *
     * @param level the server level containing the soil
     * @param pos   the position of the tillable soil block
     */
    private void tillAndPlant(ServerLevel level, BlockPos pos) {
        if (!isTillableSoil(level, pos)) {
            return;
        }

        if (!damageHoe(level)) {
            return;
        }

        level.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState());
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.NEUTRAL, 1.0F, 1.0F);

        for (Map.Entry<Block, Item> entry : FarmingUtil.cropToSeedMap().entrySet()) {
            if (this.minion.consumeItem(entry.getValue(), 1)) {
                level.setBlockAndUpdate(pos.above(), entry.getKey().defaultBlockState());
                break;
            }
        }
    }

    /** @return {@code true} if the minion holds or carries a hoe */
    private boolean hasHoe() {
        return findHoeHandSlot() != null || findHoeInventorySlot() >= 0;
    }

    /**
     * Applies one point of durability damage to the minion's hoe, preferring
     * an equipped hoe over one found in the inventory.
     *
     * @param level the server level, used for the item-breaking callback
     * @return {@code true} if a hoe was found and damaged, {@code false} if the minion has no hoe
     */
    private boolean damageHoe(ServerLevel level) {
        EquipmentSlot handSlot = findHoeHandSlot();
        if (handSlot != null) {
            ItemStack hoeStack = this.minion.getItemBySlot(handSlot);
            hoeStack.hurtAndBreak(1, level, null, item -> {});
            this.minion.setItemSlot(handSlot, hoeStack.isEmpty() ? ItemStack.EMPTY : hoeStack);
            return true;
        }

        int invSlot = findHoeInventorySlot();
        if (invSlot >= 0) {
            ItemStack hoeStack = this.minion.getItem(invSlot);
            hoeStack.hurtAndBreak(1, level, null, item -> {});
            this.minion.setItem(invSlot, hoeStack.isEmpty() ? ItemStack.EMPTY : hoeStack);
            return true;
        }

        return false;
    }

    /** @return the equipment slot (main hand or offhand) holding a hoe, or {@code null} if none */
    private @Nullable EquipmentSlot findHoeHandSlot() {
        if (FarmingUtil.isHoe(this.minion.getItemBySlot(EquipmentSlot.MAINHAND))) {
            return EquipmentSlot.MAINHAND;
        }
        if (FarmingUtil.isHoe(this.minion.getItemBySlot(EquipmentSlot.OFFHAND))) {
            return EquipmentSlot.OFFHAND;
        }
        return null;
    }

    /** @return the inventory slot index holding a hoe, or {@code -1} if none */
    private int findHoeInventorySlot() {
        for (int slot = 0; slot < this.minion.getContainerSize(); slot++) {
            ItemStack stack = this.minion.getItem(slot);
            if (!stack.isEmpty() && FarmingUtil.isHoe(stack)) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * @param level the level to read block states from
     * @param pos   the position to check
     * @return {@code true} if {@code pos} is tillable soil, has air above it,
     *         and is within {@link #MAX_WATER_DISTANCE} of water
     */
    private boolean isTillableSoil(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!FarmingUtil.isTillableSoil(state)) {
            return false;
        }
        if (!level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        return isNearWater(level, pos);
    }

    /**
     * @param level the level to check fluid states in
     * @param pos   the position to search around
     * @return {@code true} if a water block exists within {@link #MAX_WATER_DISTANCE}
     *         blocks horizontally (and one block vertically) of {@code pos}
     */
    private boolean isNearWater(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -MAX_WATER_DISTANCE; dx <= MAX_WATER_DISTANCE; dx++) {
            for (int dz = -MAX_WATER_DISTANCE; dz <= MAX_WATER_DISTANCE; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    cursor.setWithOffset(pos, dx, dy, dz);
                    if (level.getFluidState(cursor).is(Fluids.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Scans a box around the minion (see {@link #horizontalRadius} and
     * {@link #VERTICAL_RADIUS}) for the closest tillable soil block near water.
     *
     * @return the nearest tillable soil position, or {@code null} if none was found
     */
    private @Nullable BlockPos findNearestTillableSoil() {
        Level level = this.minion.level();
        BlockPos origin = this.minion.blockPosition();
        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (int dx = -this.horizontalRadius; dx <= this.horizontalRadius; dx++) {
            for (int dz = -this.horizontalRadius; dz <= this.horizontalRadius; dz++) {
                for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (isTillableSoil(level, pos)) {
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