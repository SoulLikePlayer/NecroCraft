package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.world.entity.minion.registry.MinionEvolution;
import net.necrocraft.world.inventory.CarvingMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CarvingScreen extends AbstractContainerScreen<@NotNull CarvingMenu> {
    private static final Identifier CARVING_TABLE_LOCATION_EQUIPMENT = Identifier.fromNamespaceAndPath("necrocraft", "textures/gui/container/soul_carving_table/equipment.png");
    private static final Identifier CARVING_TABLE_LOCATION_BONUS = Identifier.fromNamespaceAndPath("necrocraft", "textures/gui/container/soul_carving_table/bonus.png");
    private static final Identifier CARVING_TABLE_LOCATION_EVOLUTION = Identifier.fromNamespaceAndPath("necrocraft", "textures/gui/container/soul_carving_table/evolution.png");

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
    private static final Component EVOLUTION_INGREDIENT_TOOLTIP = Component.translatable("carving.necrocraft.slot.evolution_ingredient");
    private static final Component EVOLUTION_RESULT_TOOLTIP = Component.translatable("carving.necrocraft.slot.evolution_result");

    private static final int EVOLUTION_TOTEM_X = 8;
    private static final int EVOLUTION_TOTEM_Y = 20;
    private static final int EVOLUTION_INGREDIENT_X = 8;
    private static final int EVOLUTION_INGREDIENT_Y = 106;

    private static final int EVOLUTION_RESULT_X = 152;
    private static final int EVOLUTION_RESULT_Y = 63;

    private static final int EVOLUTION_LIST_X = 44;
    private static final int EVOLUTION_LIST_Y = 20;
    private static final int EVOLUTION_LIST_WIDTH = 88;
    private static final int EVOLUTION_LIST_OPTION_HEIGHT = 16;
    private static final int EVOLUTION_LIST_GAP = 2;

    private static final Component EQUIPMENT_PAGE_TITLE = Component.translatable("carving.necrocraft.page.equipment");
    private static final Component BONUS_PAGE_TITLE = Component.translatable("carving.necrocraft.page.bonus");
    private static final Component EVOLUTION_PAGE_TITLE = Component.translatable("carving.necrocraft.page.evolution");

    private static final Component EQUIPMENT_PAGE_TOOLTIP = Component.translatable("carving.necrocraft.page.equipment.tooltip");
    private static final Component BONUS_PAGE_TOOLTIP = Component.translatable("carving.necrocraft.page.bonus.tooltip");
    private static final Component EVOLUTION_PAGE_TOOLTIP = Component.translatable("carving.necrocraft.page.evolution.tooltip");

    private static final Component TITLE_SEPARATOR = Component.literal(" – ");

    private static final int HIDDEN = -1000;

    private enum Page { EQUIPMENT, BONUS, EVOLUTION }

    private static final Page[] PAGE_ORDER = { Page.EQUIPMENT, Page.BONUS, Page.EVOLUTION };

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

    private final List<Button> evolutionOptionButtons = new ArrayList<>();

    private List<MinionEvolution> lastEvolutions = List.of();
    private int lastSelectedEvolutionIndex = -1;
    private ItemStack lastIngredientStack = ItemStack.EMPTY;

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
        this.prevPageButton = Button.builder(Component.literal("<"), b -> this.goToAdjacentPage(-1))
                .bounds(this.leftPos - 18, arrowY, arrowWidth, arrowHeight)
                .build();
        this.nextPageButton = Button.builder(Component.literal(">"), b -> this.goToAdjacentPage(1))
                .bounds(this.leftPos + this.imageWidth - arrowWidth + 18, arrowY, arrowWidth, arrowHeight)
                .build();

        this.addRenderableWidget(this.prevPageButton);
        this.addRenderableWidget(this.nextPageButton);

        this.setPage(this.currentPage);
    }

    private void goToAdjacentPage(int delta) {
        int index = indexOf(this.currentPage) + delta;
        if (index >= 0 && index < PAGE_ORDER.length) {
            this.setPage(PAGE_ORDER[index]);
        }
    }

    private static int indexOf(Page page) {
        for (int i = 0; i < PAGE_ORDER.length; i++) {
            if (PAGE_ORDER[i] == page) {
                return i;
            }
        }
        return -1;
    }

    private void setPage(Page page) {
        this.currentPage = page;

        Component pageLabel = switch (page) {
            case EQUIPMENT -> EQUIPMENT_PAGE_TITLE;
            case BONUS -> BONUS_PAGE_TITLE;
            case EVOLUTION -> EVOLUTION_PAGE_TITLE;
        };
        this.setScreenTitle(Component.empty().append(this.baseTitle).append(TITLE_SEPARATOR).append(pageLabel));

        boolean showEquipment = page == Page.EQUIPMENT;
        boolean showBonus = page == Page.BONUS;
        boolean showEvolution = page == Page.EVOLUTION;

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
                    showBonus ? pos[0] : HIDDEN,
                    showBonus ? pos[1] : HIDDEN);
        }

        for (int i = 0; i < CarvingMenu.FREE_SLOT_COUNT; i++) {
            Slot freeSlot = this.menu.slots.get(CarvingMenu.FREE_SLOT_START + i);
            setSlotPos(freeSlot, HIDDEN, HIDDEN);
        }

        Slot totemSlot = this.menu.slots.get(CarvingMenu.TOTEM_SLOT);
        setSlotPos(totemSlot,
                showEvolution ? EVOLUTION_TOTEM_X : CarvingMenu.TOTEM_SLOT_X,
                showEvolution ? EVOLUTION_TOTEM_Y : CarvingMenu.TOTEM_SLOT_Y);

        Slot ingredientSlot = this.menu.slots.get(CarvingMenu.EVOLUTION_INGREDIENT_SLOT);
        setSlotPos(ingredientSlot,
                showEvolution ? EVOLUTION_INGREDIENT_X : HIDDEN,
                showEvolution ? EVOLUTION_INGREDIENT_Y : HIDDEN);

        Slot resultSlot = this.menu.slots.get(CarvingMenu.EVOLUTION_RESULT_SLOT);
        setSlotPos(resultSlot,
                showEvolution ? EVOLUTION_RESULT_X : HIDDEN,
                showEvolution ? EVOLUTION_RESULT_Y : HIDDEN);

        if (this.prevPageButton != null) {
            int index = indexOf(page);
            this.prevPageButton.active = index > 0;
            this.nextPageButton.active = index < PAGE_ORDER.length - 1;
            this.prevPageButton.setTooltip(index > 0 ? Tooltip.create(pageTooltip(PAGE_ORDER[index - 1])) : null);
            this.nextPageButton.setTooltip(index < PAGE_ORDER.length - 1 ? Tooltip.create(pageTooltip(PAGE_ORDER[index + 1])) : null);
        }

        this.lastEvolutions = List.of();
        this.lastSelectedEvolutionIndex = -1;
        this.lastIngredientStack = ItemStack.EMPTY;
        this.refreshEvolutionList();
    }

    private static Component pageTooltip(Page page) {
        return switch (page) {
            case EQUIPMENT -> EQUIPMENT_PAGE_TOOLTIP;
            case BONUS -> BONUS_PAGE_TOOLTIP;
            case EVOLUTION -> EVOLUTION_PAGE_TOOLTIP;
        };
    }

    private void rebuildEvolutionOptionButtons() {
        for (Button button : this.evolutionOptionButtons) {
            this.removeWidget(button);
        }
        this.evolutionOptionButtons.clear();

        if (this.currentPage != Page.EVOLUTION) {
            return;
        }

        List<MinionEvolution> evolutions = this.menu.getAvailableEvolutions();
        if (evolutions.isEmpty()) {
            return;
        }

        int selectedIndex = this.menu.getSelectedEvolutionIndex();
        int step = EVOLUTION_LIST_OPTION_HEIGHT + EVOLUTION_LIST_GAP;
        ItemStack ingredientStack = this.menu.slots.get(CarvingMenu.EVOLUTION_INGREDIENT_SLOT).getItem();

        for (int i = 0; i < evolutions.size(); i++) {
            MinionEvolution evolution = evolutions.get(i);
            int evolutionIndex = i;
            boolean selected = i == selectedIndex;
            boolean ingredientReady = evolution.acceptsIngredient(ingredientStack);

            Component label = selected
                    ? Component.literal("> ").append(evolutionLabel(evolution))
                    : evolutionLabel(evolution);

            Button optionButton = Button.builder(label, b -> this.selectEvolution(evolutionIndex))
                    .bounds(this.leftPos + EVOLUTION_LIST_X, this.topPos + EVOLUTION_LIST_Y + i * step,
                            EVOLUTION_LIST_WIDTH, EVOLUTION_LIST_OPTION_HEIGHT)
                    .build();
            optionButton.active = ingredientReady && !selected;
            this.evolutionOptionButtons.add(optionButton);
            this.addRenderableWidget(optionButton);
        }
    }

    private void selectEvolution(int index) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
        }
        this.lastSelectedEvolutionIndex = index;
        this.rebuildEvolutionOptionButtons();
    }

    private void refreshEvolutionList() {
        List<MinionEvolution> evolutions = this.menu.getAvailableEvolutions();
        int selectedIndex = this.menu.getSelectedEvolutionIndex();
        ItemStack ingredientStack = this.menu.slots.get(CarvingMenu.EVOLUTION_INGREDIENT_SLOT).getItem();

        boolean evolutionsChanged = evolutions != this.lastEvolutions;
        boolean selectionChanged = selectedIndex != this.lastSelectedEvolutionIndex;
        boolean ingredientChanged = !ItemStack.matches(ingredientStack, this.lastIngredientStack);

        if (evolutionsChanged || selectionChanged || ingredientChanged) {
            this.lastEvolutions = evolutions;
            this.lastSelectedEvolutionIndex = selectedIndex;
            this.lastIngredientStack = ingredientStack.copy();
            this.rebuildEvolutionOptionButtons();
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.currentPage == Page.EVOLUTION) {
            this.refreshEvolutionList();
        }
    }

    private static Component evolutionLabel(MinionEvolution evolution) {
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(evolution.result());
        String descriptionId = "entity." + id.getNamespace() + "." + id.getPath().replace('/', '.');
        return Component.translatable(descriptionId);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            this.goToAdjacentPage(-1);
            return true;
        }
        if (scrollY < 0) {
            this.goToAdjacentPage(1);
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
        Identifier texture = switch (this.currentPage) {
            case BONUS -> CARVING_TABLE_LOCATION_BONUS;
            case EVOLUTION -> CARVING_TABLE_LOCATION_EVOLUTION;
            case EQUIPMENT -> CARVING_TABLE_LOCATION_EQUIPMENT;
        };
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
        } else if (this.currentPage == Page.EVOLUTION) {
            if (slotIndex == CarvingMenu.EVOLUTION_INGREDIENT_SLOT) {
                return EVOLUTION_INGREDIENT_TOOLTIP;
            }
            if (slotIndex == CarvingMenu.EVOLUTION_RESULT_SLOT) {
                return EVOLUTION_RESULT_TOOLTIP;
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
