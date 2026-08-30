package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.necrocraft.client.NecroCraftClient;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import org.jetbrains.annotations.NotNull;

public class MinionInventoryScreen extends AbstractContainerScreen<@NotNull MinionInventoryMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/gui/container/minion_inventory.png");

    /**
     * Layout for the health and soul gauge bars drawn in the header of the
     * container screen, above the inventory grid. Positions are relative to
     * the top-left corner of the background texture ({@link #leftPos}/{@link #topPos})
     * and are placeholders — adjust the offsets to match the actual texture.
     */
    private static final int GAUGE_BAR_WIDTH = 60;
    private static final int GAUGE_BAR_HEIGHT = 5;
    private static final int GAUGE_BAR_X_OFFSET = 8;
    private static final int GAUGE_BAR_SPACING = 3;
    private static final int SOUL_BAR_Y_OFFSET = -GAUGE_BAR_HEIGHT - GAUGE_BAR_SPACING;
    private static final int HEALTH_BAR_Y_OFFSET = SOUL_BAR_Y_OFFSET - GAUGE_BAR_HEIGHT - GAUGE_BAR_SPACING;

    private static final int HEALTH_BAR_BG_COLOR = 0xFF4B0000;
    private static final int HEALTH_BAR_FG_COLOR = 0xFFB40000;
    private static final int SOUL_BAR_BG_COLOR = 0xFF15122A;
    private static final int SOUL_BAR_FG_COLOR = 0xFF6E3FBF;

    public MinionInventoryScreen(MinionInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int xo = this.leftPos;
        int yo = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        extractMinionGauges(graphics, xo, yo);
    }

    /**
     * Draws the owning minion's health and soul gauge as two small bars in
     * the header of the container, so the player can check on both without
     * closing the screen.
     * <p>
     * NOTE: assumes {@link MinionInventoryMenu} exposes the underlying
     * {@link AbstractMinion} via {@code getMinion()} — adjust the accessor
     * name below if yours differs.
     */
    private void extractMinionGauges(@NotNull GuiGraphicsExtractor graphics, int xo, int yo) {
        AbstractMinion minion = this.menu.getMinion();
        if (minion == null || !minion.isAlive()) return;

        float maxHealth = minion.getMaxHealth();
        float healthRatio = maxHealth > 0.0F ? Mth.clamp(minion.getHealth() / maxHealth, 0.0F, 1.0F) : 0.0F;
        float soulRatio = Mth.clamp(minion.getData(ModAttachments.SOUL_GAUGE) / 100.0F, 0.0F, 1.0F);

        int barX = xo + GAUGE_BAR_X_OFFSET;
        drawGaugeBar(graphics, barX, yo + HEALTH_BAR_Y_OFFSET, healthRatio, HEALTH_BAR_BG_COLOR, HEALTH_BAR_FG_COLOR);
        drawGaugeBar(graphics, barX, yo + SOUL_BAR_Y_OFFSET, soulRatio, SOUL_BAR_BG_COLOR, SOUL_BAR_FG_COLOR);
    }

    private void drawGaugeBar(@NotNull GuiGraphicsExtractor graphics, int x, int y, float ratio, int bgColor, int fgColor) {
        graphics.fill(x, y, x + GAUGE_BAR_WIDTH, y + GAUGE_BAR_HEIGHT, bgColor);
        int filledWidth = (int) (GAUGE_BAR_WIDTH * ratio);
        if (filledWidth > 0) {
            graphics.fill(x, y, x + filledWidth, y + GAUGE_BAR_HEIGHT, fgColor);
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}