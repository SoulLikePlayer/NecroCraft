package net.necrocraft.world.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import net.necrocraft.world.item.bonus.BonusUtil;
import net.necrocraft.world.item.component.SoulData;
import net.necrocraft.world.item.equipment.SoulTotem;
import net.necrocraft.world.level.block.ModBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CarvingMenu extends AbstractContainerMenu {

    public static final int TOTEM_SLOT = 0;
    public static final int TOTEM_SLOT_X = 80;
    public static final int TOTEM_SLOT_Y = 63;

    public static final int FREE_SLOT_START = 1;
    public static final int FREE_SLOT_COUNT = 8;
    private static final int FREE_SLOT_END = FREE_SLOT_START + FREE_SLOT_COUNT;

    public static final int[][] FREE_SLOT_POSITIONS = {
            {152, 12},
            {152, 30},
            {152, 48},
            {152, 66},
            {152, 84},
            {152, 102},
            {152, 120},
            {152, 138}
    };

    public static final int EQUIPMENT_SLOT_START = FREE_SLOT_END;
    public static final int EQUIPMENT_SLOT_COUNT = SoulData.EQUIPMENT_SIZE;
    private static final int EQUIPMENT_SLOT_END = EQUIPMENT_SLOT_START + EQUIPMENT_SLOT_COUNT;

    public static final int[][] EQUIPMENT_SLOT_POSITIONS = {
            {8, 18},
            {8, 36},
            {8, 54},
            {8, 72},
            {8, 90},
            {8, 108},
    };

    public static final int BONUS_SLOT_START = EQUIPMENT_SLOT_END;
    public static final int BONUS_SLOT_COUNT = SoulData.MAX_BONUSES;
    private static final int BONUS_SLOT_END = BONUS_SLOT_START + BONUS_SLOT_COUNT;

    public static final int[][] BONUS_SLOT_POSITIONS = {
            {80, 12},
            {44, 28},
            {116, 28},
            {29, 62},
            {130, 62},
            {44, 99},
            {116, 99},
            {80, 113}
    };

    private static final int CONTAINER_SIZE = BONUS_SLOT_END;

    private static final int INV_SLOT_START = CONTAINER_SIZE;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int HOTBAR_SLOT_START = INV_SLOT_END;
    private static final int HOTBAR_SLOT_END = HOTBAR_SLOT_START + 9;

    private static final EquipmentSlot[] EQUIPMENT_ORDER = new EquipmentSlot[] {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };


    private final ContainerLevelAccess access;
    private final Container container;

    public CarvingMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(CONTAINER_SIZE), ContainerLevelAccess.NULL);
    }

    public CarvingMenu(int containerId, Inventory inventory, Container container, ContainerLevelAccess access) {
        super(ModMenuTypes.CARVING_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        container.startOpen(inventory.player);

        this.access = access;
        this.container = container;

        this.addSlot(new TotemSlot(container, TOTEM_SLOT, TOTEM_SLOT_X, TOTEM_SLOT_Y));

        for (int i = 0; i < FREE_SLOT_COUNT; i++) {
            this.addSlot(new Slot(container, FREE_SLOT_START + i, FREE_SLOT_POSITIONS[i][0], FREE_SLOT_POSITIONS[i][1]));
        }

        for (int i = 0; i < EQUIPMENT_SLOT_COUNT; i++) {
            int[] pos = EQUIPMENT_SLOT_POSITIONS[i];
            this.addSlot(new EquipmentColumnSlot(container, EQUIPMENT_SLOT_START + i, pos[0], pos[1], EQUIPMENT_ORDER[i]));
        }

        for (int i = 0; i < BONUS_SLOT_COUNT; i++) {
            int[] pos = BONUS_SLOT_POSITIONS[i];
            this.addSlot(new BonusSlot(container, BONUS_SLOT_START + i, pos[0], pos[1]));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 198));
        }
    }

    private class TotemSlot extends Slot {
        public TotemSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof SoulTotem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void set(@NotNull ItemStack itemStack) {
            super.set(itemStack);
            if (!itemStack.isEmpty()){
                SoulData soulData = itemStack.get(ModDataComponents.SOUL_DATA.get());
                if(soulData != null){
                    List<ItemStack> equipment = soulData.equipment();
                    for (int i = 0; i < EQUIPMENT_SLOT_COUNT && i < equipment.size(); i++){
                        ItemStack piece = equipment.get(i);
                        if (!piece.isEmpty() && CarvingMenu.this.container.getItem(EQUIPMENT_SLOT_START + i).isEmpty()) {
                            CarvingMenu.this.container.setItem(EQUIPMENT_SLOT_START + i, piece.copy());
                        }
                    }

                    List<Identifier> bonuses = soulData.bonuses();
                    for (int i = 0; i < BONUS_SLOT_COUNT && i < bonuses.size(); i++) {
                        Identifier bonusId = bonuses.get(i);
                        if (CarvingMenu.this.container.getItem(BONUS_SLOT_START + i).isEmpty()) {
                            int finalI = i;
                            BonusUtil.resolve(bonusId)
                                    .ifPresent(bonus -> CarvingMenu.this.container.setItem(
                                            BONUS_SLOT_START + finalI, new ItemStack(bonus)));
                        }
                    }
                }
            }
        }

        @Override
        public void onTake(@NotNull Player player, ItemStack stack) {
            SoulData soulData = stack.get(ModDataComponents.SOUL_DATA.get());
            if (soulData != null) {
                List<ItemStack> equipment = new ArrayList<>(EQUIPMENT_SLOT_COUNT);
                boolean hasAnyEquipment = false;
                for (int i = 0; i < EQUIPMENT_SLOT_COUNT; i++) {
                    ItemStack piece = CarvingMenu.this.container.getItem(EQUIPMENT_SLOT_START + i).copy();
                    if (!piece.isEmpty()) {
                        hasAnyEquipment = true;
                    }
                    equipment.add(piece);
                }

                List<Identifier> bonuses = new ArrayList<>();
                for (int i = 0; i < BONUS_SLOT_COUNT; i++) {
                    ItemStack piece = CarvingMenu.this.container.getItem(BONUS_SLOT_START + i);
                    if (!piece.isEmpty() && piece.getItem() instanceof AbstractBonusItem) {
                        Identifier bonusId = BuiltInRegistries.ITEM.getKey(piece.getItem());
                        if (!bonuses.contains(bonusId)) {
                            bonuses.add(bonusId);
                        }
                    }
                    CarvingMenu.this.container.setItem(BONUS_SLOT_START + i, ItemStack.EMPTY);
                }

                SoulData updated = hasAnyEquipment ? soulData.withEquipment(equipment) : soulData;
                updated = updated.withBonuses(bonuses);
                if (updated != soulData) {
                    stack.set(ModDataComponents.SOUL_DATA.get(), updated);
                }

                for (int i = 0; i < EQUIPMENT_SLOT_COUNT; i++) {
                    CarvingMenu.this.container.setItem(EQUIPMENT_SLOT_START + i, ItemStack.EMPTY);
                }
            }
            super.onTake(player, stack);
        }
    }

    private class EquipmentColumnSlot extends Slot {
        private final EquipmentSlot equipmentSlot;

        public EquipmentColumnSlot(Container container, int index, int x, int y, EquipmentSlot equipmentSlot) {
            super(container, index, x, y);
            this.equipmentSlot = equipmentSlot;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            if (this.equipmentSlot == EquipmentSlot.MAINHAND || this.equipmentSlot == EquipmentSlot.OFFHAND) {
                return true;
            }
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            return equippable != null && equippable.slot() == this.equipmentSlot;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            CarvingMenu.this.updateSoulData();
        }
    }

    private class BonusSlot extends Slot {
        public BonusSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (!(stack.getItem() instanceof AbstractBonusItem bonus_item_watend)) {
                return false;
            }
            for (int i = BONUS_SLOT_START; i < BONUS_SLOT_END; i++) {
                if (i == this.getContainerSlot()) {
                    continue;
                }
                ItemStack placed_bonus = CarvingMenu.this.container.getItem(i);
                if (!placed_bonus.isEmpty()) {
                    if(placed_bonus.getItem() == stack.getItem()){
                        return false;
                    }

                    if (!(placed_bonus.getItem() instanceof AbstractBonusItem bonus_item_placed)) {
                        continue;
                    }

                    BonusType wanted = bonus_item_watend.getBonusTypes();
                    if (wanted != BonusType.PASSIVE && bonus_item_placed.getBonusTypes() == wanted) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            CarvingMenu.this.updateSoulData();
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, ModBlock.SOUL_CARVING_TABLE_BLOCK.get());
    }

    @Override
    public void removed(@NotNull Player player) {
        player.getInventory().placeItemBackInInventory(this.slots.get(TOTEM_SLOT).getItem());
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved = false;

                if (stackInSlot.getItem() instanceof SoulTotem) {
                    moved = this.moveItemStackTo(stackInSlot, TOTEM_SLOT, TOTEM_SLOT + 1, false);
                }

                if (!moved && stackInSlot.getItem() instanceof AbstractBonusItem) {
                    moved = this.moveItemStackTo(stackInSlot, BONUS_SLOT_START, BONUS_SLOT_END, false);
                }

                if (!moved) {
                    Equippable equippable = stackInSlot.get(DataComponents.EQUIPPABLE);
                    if (equippable != null) {
                        int equipIndex = equipmentIndexFor(equippable.slot());
                        if (equipIndex >= 0) {
                            moved = this.moveItemStackTo(stackInSlot, equipIndex, equipIndex + 1, false);
                        }
                    }
                }

                if (!moved) {
                    moved = this.moveItemStackTo(stackInSlot, FREE_SLOT_START, FREE_SLOT_END, false);
                }

                if (!moved) {
                    if (index < INV_SLOT_END) {
                        moved = this.moveItemStackTo(stackInSlot, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false);
                    } else {
                        moved = this.moveItemStackTo(stackInSlot, INV_SLOT_START, INV_SLOT_END, false);
                    }
                }

                if (!moved) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return result;
    }

    private void updateSoulData() {
        ItemStack totemStack = this.container.getItem(TOTEM_SLOT);
        if (totemStack.isEmpty()) {
            return;
        }
        SoulData soulData = totemStack.get(ModDataComponents.SOUL_DATA.get());
        if (soulData == null) {
            return;
        }
        List<ItemStack> equipment = new ArrayList<>(EQUIPMENT_SLOT_COUNT);
        for (int i = 0; i < EQUIPMENT_SLOT_COUNT; i++) {
            equipment.add(this.container.getItem(EQUIPMENT_SLOT_START + i).copy());
        }

        List<Identifier> bonuses = new ArrayList<>();
        for (int i = 0; i < BONUS_SLOT_COUNT; i++) {
            ItemStack piece = this.container.getItem(BONUS_SLOT_START + i);
            if (!piece.isEmpty() && piece.getItem() instanceof AbstractBonusItem) {
                Identifier bonusId = BuiltInRegistries.ITEM.getKey(piece.getItem());
                bonuses.add(bonusId);
            }
        }

        totemStack.set(ModDataComponents.SOUL_DATA.get(),
                soulData.withEquipment(equipment).withBonuses(bonuses));
    }

    private static int equipmentIndexFor(EquipmentSlot equipmentSlot) {
        for (int i = 0; i < EQUIPMENT_ORDER.length; i++) {
            if (EQUIPMENT_ORDER[i] == equipmentSlot) {
                return EQUIPMENT_SLOT_START + i;
            }
        }
        return -1;
    }
}