package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.necrocraft.world.inventory.CarvingMenu;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class CarvingScreen extends AbstractContainerScreen<@NotNull CarvingMenu> {
    private static final Identifier CARVING_TABLE_LOCATION_EQUIPMENT = Identifier.fromNamespaceAndPath("necrocraft", "textures/gui/container/soul_carving_table/equipment.png");
    private static final Identifier CARVING_TABLE_LOCATION_BONUS = Identifier.fromNamespaceAndPath("necrocraft", "textures/gui/container/soul_carving_table/bonus.png");

    private static final Component TOTEM_TOOLTIP = Component.translatable("carving.necrocraft.slot.totem");

    private static final Component[] EQUIPMENT_TOOLTIPS = new Component[] {
            Component.translatable("carving.necrocraft.slot.head"),
            Component.translatable("carving.necrocraft.slot.chest"),
            Component.translatable("carving.necrocraft.slot.legs"),
            Component.translatable("carving.necrocraft.slot.feet"),
            Component.translatable("carving.necrocraft.slot.mainhand"),
            Component.translatable("carving.necrocraft.slot.offhand"),
    };

    private static final Component BONUS_TOOLTIP = Component.translatable("carving.necrocraft.slot.bonus");

    private static final Component EQUIPMENT_PAGE_TITLE = Component.translatable("carving.necrocraft.page.equipment");
    private static final Component BONUS_PAGE_TITLE = Component.translatable("carving.necrocraft.page.bonus");

    private static final Component EQUIPMENT_PAGE_TOOLTIP = Component.translatable("carving.necrocraft.page.equipment.tooltip");
    private static final Component BONUS_PAGE_TOOLTIP = Component.translatable("carving.necrocraft.page.bonus.tooltip");

    private static final Component TITLE_SEPARATOR = Component.literal(" – ");

    private static final int HIDDEN = -1000;

    private enum Page { EQUIPMENT, BONUS }

    private static final Field SLOT_X_FIELD = findField(Slot.class, "x");
    private static final Field SLOT_Y_FIELD = findField(Slot.class, "y");
    private static final Field SCREEN_TITLE_FIELD = findField(Screen.class, "title");

    private static Field findField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void setSlotPos(Slot slot, int x, int y) {
        try {
            SLOT_X_FIELD.setInt(slot, x);
            SLOT_Y_FIELD.setInt(slot, y);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossible de repositionner le slot via reflexion", e);
        }
    }

    private void setScreenTitle(Component title) {
        try {
            SCREEN_TITLE_FIELD.set(this, title);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossible de changer le titre via reflexion", e);
        }
    }

    private final Component baseTitle;

    private Page currentPage = Page.EQUIPMENT;

    private Button prevPageButton;
    private Button nextPageButton;

    public CarvingScreen(CarvingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 222);
        this.inventoryLabelY = this.imageHeight - 94;
        this.baseTitle = title;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;

        int arrowWidth = 16;
        int arrowHeight = 140;
        int arrowY = this.topPos + 4;
        this.prevPageButton = Button.builder(Component.literal("<"), b -> this.setPage(Page.EQUIPMENT))
                .bounds(this.leftPos - 18, arrowY, arrowWidth, arrowHeight)
                .tooltip(Tooltip.create(EQUIPMENT_PAGE_TOOLTIP))
                .build();
        this.nextPageButton = Button.builder(Component.literal(">"), b -> this.setPage(Page.BONUS))
                .bounds(this.leftPos + this.imageWidth - arrowWidth + 18, arrowY, arrowWidth, arrowHeight)
                .tooltip(Tooltip.create(BONUS_PAGE_TOOLTIP))
                .build();

        this.addRenderableWidget(this.prevPageButton);
        this.addRenderableWidget(this.nextPageButton);

        this.setPage(this.currentPage);
    }

    private void setPage(Page page) {
        this.currentPage = page;

        Component pageLabel = page == Page.EQUIPMENT ? EQUIPMENT_PAGE_TITLE : BONUS_PAGE_TITLE;
        this.setScreenTitle(Component.empty().append(this.baseTitle).append(TITLE_SEPARATOR).append(pageLabel));

        boolean showEquipment = page == Page.EQUIPMENT;

        for (int i = 0; i < CarvingMenu.EQUIPMENT_SLOT_COUNT; i++) {
            Slot equipSlot = this.menu.slots.get(CarvingMenu.EQUIPMENT_SLOT_START + i);
            int[] pos = CarvingMenu.EQUIPMENT_SLOT_POSITIONS[i];
            setSlotPos(equipSlot,
                    showEquipment ? pos[0] : HIDDEN,
                    showEquipment ? pos[1] : HIDDEN);
        }

        for (int i = 0; i < CarvingMenu.BONUS_SLOT_COUNT; i++) {
            Slot bonusSlot = this.menu.slots.get(CarvingMenu.BONUS_SLOT_START + i);
            int[] pos = CarvingMenu.BONUS_SLOT_POSITIONS[i];
            setSlotPos(bonusSlot,
                    showEquipment ? HIDDEN : pos[0],
                    showEquipment ? HIDDEN : pos[1]);
        }

        for (int i = 0; i < CarvingMenu.FREE_SLOT_COUNT; i++) {
            Slot freeSlot = this.menu.slots.get(CarvingMenu.FREE_SLOT_START + i);
            setSlotPos(freeSlot, HIDDEN, HIDDEN);
        }

        if (this.prevPageButton != null) {
            this.prevPageButton.active = page != Page.EQUIPMENT;
            this.nextPageButton.active = page != Page.BONUS;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && this.currentPage != Page.EQUIPMENT) {
            this.setPage(Page.EQUIPMENT);
            return true;
        }
        if (scrollY < 0 && this.currentPage != Page.BONUS) {
            this.setPage(Page.BONUS);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.extractSlotTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int xo = this.leftPos;
        int yo = this.topPos;
        Identifier texture = this.currentPage == Page.BONUS
                ? CARVING_TABLE_LOCATION_BONUS
                : CARVING_TABLE_LOCATION_EQUIPMENT;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    private void extractSlotTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot == null || this.hoveredSlot.hasItem()) {
            return;
        }

        Component tooltip = getEmptySlotTooltip(this.hoveredSlot.index);
        if (tooltip != null) {
            graphics.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
        }
    }

    private Component getEmptySlotTooltip(int slotIndex) {
        if (slotIndex == CarvingMenu.TOTEM_SLOT) {
            return TOTEM_TOOLTIP;
        }

        if (this.currentPage == Page.BONUS) {
            int bonusIndex = slotIndex - CarvingMenu.BONUS_SLOT_START;
            if (bonusIndex >= 0 && bonusIndex < CarvingMenu.BONUS_SLOT_COUNT) {
                return BONUS_TOOLTIP;
            }
        } else {
            int equipIndex = slotIndex - CarvingMenu.EQUIPMENT_SLOT_START;
            if (equipIndex >= 0 && equipIndex < EQUIPMENT_TOOLTIPS.length) {
                return EQUIPMENT_TOOLTIPS[equipIndex];
            }
        }

        return null;
    }
}