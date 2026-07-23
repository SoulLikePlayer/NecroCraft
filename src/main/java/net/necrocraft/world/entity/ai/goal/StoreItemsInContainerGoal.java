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
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.BonusUtil;
import net.necrocraft.world.item.bonus.impl.StorageBonusItem;

import java.util.EnumSet;


public class StoreItemsInContainerGoal extends Goal {

    private static final int SEARCH_HORIZONTAL_RADIUS = 8;
    private static final int SEARCH_VERTICAL_RADIUS = 4;
    private static final int SEARCH_COOLDOWN = 100;
    private static final double REACH_DISTANCE_SQ = 3.0D * 3.0D;

    private final AbstractMinion minion;
    private final double speedModifier;

    private BlockPos targetContainerPos;
    private int searchCooldown;

    public StoreItemsInContainerGoal(AbstractMinion minion, double speedModifier) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        NecroCraft.LOGGER.info(String.valueOf(hasStorageBonus()));
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

    @Override
    public boolean canContinueToUse() {
        return hasStorageBonus()
                && this.targetContainerPos != null
                && !this.minion.isEmpty()
                && isValidContainer(this.minion.level(), this.targetContainerPos);
    }

    @Override
    public void start() {
        moveTowardTarget();
    }

    @Override
    public void stop() {
        this.targetContainerPos = null;
        this.minion.getNavigation().stop();
    }

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
        } else {
            this.minion.getNavigation().stop();
            this.minion.getLookControl().setLookAt(
                    this.targetContainerPos.getX() + 0.5D,
                    this.targetContainerPos.getY() + 0.5D,
                    this.targetContainerPos.getZ() + 0.5D
            );

            if (this.minion.level() instanceof ServerLevel serverLevel) {
                depositInventory(serverLevel, this.targetContainerPos);
            }
            this.targetContainerPos = null;
        }
    }

    private void moveTowardTarget() {
        this.minion.getNavigation().moveTo(
                this.targetContainerPos.getX() + 0.5D,
                this.targetContainerPos.getY(),
                this.targetContainerPos.getZ() + 0.5D,
                this.speedModifier
        );
    }

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

    private boolean isValidContainer(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof BarrelBlock;
    }

    private void depositInventory(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof Container container)) {
            return;
        }

        boolean depositedSomething = false;

        for (int slot = 0; slot < this.minion.getContainerSize(); slot++) {
            ItemStack stack = this.minion.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            ItemStack remainder = insertIntoContainer(container, stack);
            this.minion.setItem(slot, remainder);
            if (remainder.getCount() != stack.getCount()) {
                depositedSomething = true;
            }
        }

        if (depositedSomething) {
            level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.NEUTRAL, 0.5F, 1.0F);
        }
    }

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

    private boolean hasStorageBonus() {
        for (Identifier bonusId : this.minion.getBonuses()) {
            if (BonusUtil.resolve(bonusId).filter(StorageBonusItem.class::isInstance).isPresent()) {
                return true;
            }
        }
        return false;
    }
}