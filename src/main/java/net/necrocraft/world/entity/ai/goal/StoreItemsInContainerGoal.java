package net.necrocraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.BonusUtil;
import net.necrocraft.world.item.bonus.impl.StorageBonusItem;

import java.util.EnumSet;

/**
 * Makes a minion equipped with a {@link StorageBonusItem} bonus walk its
 * carried inventory to the nearest chest, barrel or ender chest and deposit
 * it there, playing the appropriate open/close sound and (for chests and
 * barrels) toggling the block's visual open state while it does so.
 */
public class StoreItemsInContainerGoal extends Goal {

    private static final int SEARCH_HORIZONTAL_RADIUS = 8;
    private static final int SEARCH_VERTICAL_RADIUS = 4;
    private static final int SEARCH_COOLDOWN = 100;
    private static final double REACH_DISTANCE_SQ = 3.0D * 3.0D;

    private static final int OPEN_ANIMATION_DELAY = 10;

    private final AbstractMinion minion;
    private final double speedModifier;

    private BlockPos targetContainerPos;
    private int searchCooldown;

    private int openDelayTicks = -1;
    private boolean containerVisuallyOpened = false;

    /**
     * @param minion        the minion that should deposit its inventory into containers
     * @param speedModifier movement speed multiplier applied while walking to the container
     */
    public StoreItemsInContainerGoal(AbstractMinion minion, double speedModifier) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Determines whether the goal should start: the minion must have the
     * storage bonus, carry at least one item, and a search must locate a
     * reachable container (subject to {@link #SEARCH_COOLDOWN}).
     *
     * @return {@code true} if a target container was found and the goal should start
     */
    @Override
    public boolean canUse() {
        if (!hasStorageBonus()) {
            return false;
        }
        if (this.minion.isEmpty()) {
            return false;
        }
        if (--this.searchCooldown > 0) {
            return false;
        }
        this.searchCooldown = reducedTickDelay(SEARCH_COOLDOWN);

        this.targetContainerPos = findNearestContainer();
        return this.targetContainerPos != null;
    }

    /**
     * @return {@code true} if the minion still has the storage bonus, still
     *         carries items, and its target position still holds a valid container
     */
    @Override
    public boolean canContinueToUse() {
        return hasStorageBonus()
                && this.targetContainerPos != null
                && !this.minion.isEmpty()
                && isValidContainer(this.minion.level(), this.targetContainerPos);
    }

    /** Resets the open-animation timer and starts moving toward the target container. */
    @Override
    public void start() {
        this.openDelayTicks = -1;
        moveTowardTarget();
    }

    /**
     * Closes the container visually if it was opened, then clears all
     * goal state (target position, opened-chest marker, navigation).
     */
    @Override
    public void stop() {
        if (this.containerVisuallyOpened && this.targetContainerPos != null
                && this.minion.level() instanceof ServerLevel serverLevel) {
            setContainerVisuallyOpen(serverLevel, this.targetContainerPos, false);
        }
        this.targetContainerPos = null;
        this.openDelayTicks = -1;
        this.minion.clearOpenedChestPos();
        this.minion.getNavigation().stop();
    }

    /**
     * Drives the goal's state machine each tick: walks toward the container
     * until in reach, opens it (with a short animation delay), deposits the
     * minion's inventory, then closes the container and clears the target.
     */
    @Override
    public void tick() {
        if (this.targetContainerPos == null) {
            return;
        }

        double distanceSq = this.minion.blockPosition().distSqr(this.targetContainerPos);
        if (distanceSq > REACH_DISTANCE_SQ) {
            if (this.minion.getNavigation().isDone()) {
                moveTowardTarget();
            }
            return;
        }

        this.minion.getNavigation().stop();
        this.minion.getLookControl().setLookAt(
                this.targetContainerPos.getX() + 0.5D,
                this.targetContainerPos.getY() + 0.5D,
                this.targetContainerPos.getZ() + 0.5D
        );

        if (this.openDelayTicks < 0) {
            this.minion.setOpenedChestPos(this.targetContainerPos);
            if (this.minion.level() instanceof ServerLevel serverLevel) {
                setContainerVisuallyOpen(serverLevel, this.targetContainerPos, true);
            }
            this.openDelayTicks = OPEN_ANIMATION_DELAY;
            return;
        }

        if (this.openDelayTicks > 0) {
            this.openDelayTicks--;
            return;
        }

        if (this.minion.level() instanceof ServerLevel serverLevel) {
            depositInventory(serverLevel, this.targetContainerPos);
            setContainerVisuallyOpen(serverLevel, this.targetContainerPos, false);
        }

        this.minion.clearOpenedChestPos();
        this.targetContainerPos = null;
        this.openDelayTicks = -1;
    }

    /** Orders the minion's navigation to move to the currently targeted container. */
    private void moveTowardTarget() {
        this.minion.getNavigation().moveTo(
                this.targetContainerPos.getX() + 0.5D,
                this.targetContainerPos.getY(),
                this.targetContainerPos.getZ() + 0.5D,
                this.speedModifier
        );
    }

    /**
     * Scans a box around the minion (see {@link #SEARCH_HORIZONTAL_RADIUS} and
     * {@link #SEARCH_VERTICAL_RADIUS}) for the closest valid container block.
     *
     * @return the nearest valid container position, or {@code null} if none was found
     */
    private BlockPos findNearestContainer() {
        BlockPos origin = this.minion.blockPosition();
        Level level = this.minion.level();

        BlockPos best = null;
        long bestDistSq = Long.MAX_VALUE;

        for (int dx = -SEARCH_HORIZONTAL_RADIUS; dx <= SEARCH_HORIZONTAL_RADIUS; dx++) {
            for (int dz = -SEARCH_HORIZONTAL_RADIUS; dz <= SEARCH_HORIZONTAL_RADIUS; dz++) {
                for (int dy = -SEARCH_VERTICAL_RADIUS; dy <= SEARCH_VERTICAL_RADIUS; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (!isValidContainer(level, pos)) {
                        continue;
                    }
                    long distSq = (long) pos.distSqr(origin);
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        best = pos.immutable();
                    }
                }
            }
        }

        return best;
    }

    /**
     * @param level the level to read the block state from
     * @param pos   the position to check
     * @return {@code true} if the block at {@code pos} is a chest, ender chest or barrel
     */
    private boolean isValidContainer(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof BarrelBlock;
    }

    /**
     * Toggles the open/closed visual state (and matching sound) of the
     * container block at {@code pos}, dispatching to the correct behavior
     * for barrels, ender chests and regular chests.
     *
     * @param level the server level containing the block
     * @param pos   the position of the container block
     * @param open  {@code true} to open the container, {@code false} to close it
     */
    private void setContainerVisuallyOpen(ServerLevel level, BlockPos pos, boolean open) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BarrelBlock) {
            level.setBlock(pos, state.setValue(BarrelBlock.OPEN, open), Block.UPDATE_CLIENTS);
            level.playSound(null, pos,
                    open ? SoundEvents.BARREL_OPEN : SoundEvents.BARREL_CLOSE,
                    SoundSource.BLOCKS, 0.5F, open ? 1.0F : 0.9F);
        } else if (block instanceof EnderChestBlock) {
            level.blockEvent(pos, block, 1, open ? 1 : 0);
            level.playSound(null, pos,
                    open ? SoundEvents.ENDER_CHEST_OPEN : SoundEvents.ENDER_CHEST_CLOSE,
                    SoundSource.BLOCKS, 0.5F, open ? 1.0F : 0.9F);
        } else if (block instanceof ChestBlock) {
            level.blockEvent(pos, block, 1, open ? 1 : 0);
            level.playSound(null, pos,
                    open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE,
                    SoundSource.BLOCKS, 0.5F, open ? 1.0F : 0.9F);
        }

        this.containerVisuallyOpened = open;
    }

    /**
     * Empties every non-empty slot of the minion's inventory into the given
     * container, leaving back whatever could not fit.
     *
     * @param level the server level containing the container
     * @param pos   the position of the target container
     */
    private void depositInventory(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof Container container)) {
            return;
        }

        for (int slot = 0; slot < this.minion.getContainerSize(); slot++) {
            ItemStack stack = this.minion.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack remainder = insertIntoContainer(container, stack);
            this.minion.setItem(slot, remainder);
        }
    }

    /**
     * Inserts as much of {@code stack} as possible into {@code container},
     * first topping up existing matching stacks, then filling empty slots.
     *
     * @param container the container to insert into
     * @param stack     the stack to insert (mutated in place as items are moved)
     * @return whatever portion of {@code stack} could not be inserted (may be empty)
     */
    private ItemStack insertIntoContainer(Container container, ItemStack stack) {
        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            ItemStack slot = container.getItem(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                if (space > 0) {
                    int moved = Math.min(space, stack.getCount());
                    slot.grow(moved);
                    stack.shrink(moved);
                    container.setChanged();
                }
            }
        }

        for (int i = 0; i < container.getContainerSize() && !stack.isEmpty(); i++) {
            if (container.getItem(i).isEmpty()) {
                int moved = Math.min(stack.getMaxStackSize(), stack.getCount());
                ItemStack toPlace = stack.copy();
                toPlace.setCount(moved);
                container.setItem(i, toPlace);
                stack.shrink(moved);
                container.setChanged();
            }
        }

        return stack;
    }

    /**
     * @return {@code true} if the minion currently carries a bonus that
     *         resolves to a {@link StorageBonusItem}
     */
    private boolean hasStorageBonus() {
        for (Identifier bonusId : this.minion.getBonuses()) {
            if (BonusUtil.resolve(bonusId).filter(StorageBonusItem.class::isInstance).isPresent()) {
                return true;
            }
        }
        return false;
    }
}