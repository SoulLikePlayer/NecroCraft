package net.necrocraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

public class MinionInventoryMenu extends AbstractContainerMenu {

    private static final int MINION_INVENTORY_ROWS = 3;
    private static final int MINION_INVENTORY_COLUMNS = 9;
    private static final int MINION_SLOT_COUNT = MINION_INVENTORY_ROWS * MINION_INVENTORY_COLUMNS;

    private final Container minionContainer;

    public MinionInventoryMenu(int containerId, @NotNull Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(MINION_SLOT_COUNT));
    }

    public MinionInventoryMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Container container) {
        super(ModMenuTypes.MINION_INVENTORY.get(), containerId);
        this.minionContainer = container;
        checkContainerSize(container, MINION_SLOT_COUNT);
        container.startOpen(playerInventory.player);

        int minionGridStartX = 8;
        int minionGridStartY = 18;
        for (int row = 0; row < MINION_INVENTORY_ROWS; ++row) {
            for (int column = 0; column < MINION_INVENTORY_COLUMNS; ++column) {
                this.addSlot(new Slot(container, column + row * MINION_INVENTORY_COLUMNS,
                        minionGridStartX + column * 18, minionGridStartY + row * 18));
            }
        }

        int playerInvStartY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        8 + column * 18, playerInvStartY + row * 18));
            }
        }
        int hotbarY = playerInvStartY + 58;
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, hotbarY));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < MINION_SLOT_COUNT) {
                if (!this.moveItemStackTo(stackInSlot, MINION_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, MINION_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.minionContainer.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.minionContainer.stopOpen(player);
    }
}