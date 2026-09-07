package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import org.jetbrains.annotations.NotNull;

public class MinionInventoryScreen extends AbstractContainerScreen<@NotNull MinionInventoryMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/gui/container/minion_inventory.png");

    private static final Identifier HEALTH_BAR_BG_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "container/minion_health_background");
    private static final Identifier HEALTH_BAR_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "container/minion_health_progress");
    private static final Identifier SOUL_BAR_BG_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "container/minion_soul_background");
    private static final Identifier SOUL_BAR_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "container/minion_soul_progress");

    private static final int GAUGE_BAR_WIDTH = 60;
    private static final int GAUGE_BAR_HEIGHT = 5;
    private static final int GAUGE_BAR_X_OFFSET = 8;
    private static final int GAUGE_BAR_SPACING = 3;
    private static final int SOUL_BAR_Y_OFFSET = -GAUGE_BAR_HEIGHT - GAUGE_BAR_SPACING;
    private static final int HEALTH_BAR_Y_OFFSET = SOUL_BAR_Y_OFFSET - GAUGE_BAR_HEIGHT - GAUGE_BAR_SPACING;

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

    private void extractMinionGauges(@NotNull GuiGraphicsExtractor graphics, int xo, int yo) {
        AbstractMinion minion = this.menu.getMinion();
        if (minion == null || !minion.isAlive()) return;

        float maxHealth = minion.getMaxHealth();
        float healthRatio = maxHealth > 0.0F ? Mth.clamp(minion.getHealth() / maxHealth, 0.0F, 1.0F) : 0.0F;
        float soulRatio = Mth.clamp(minion.getData(ModAttachments.SOUL_GAUGE) / 100.0F, 0.0F, 1.0F);

        int barX = xo + GAUGE_BAR_X_OFFSET;
        drawHorizontalGaugeSprite(graphics, barX, yo + HEALTH_BAR_Y_OFFSET, healthRatio, HEALTH_BAR_BG_SPRITE, HEALTH_BAR_PROGRESS_SPRITE);
        drawHorizontalGaugeSprite(graphics, barX, yo + SOUL_BAR_Y_OFFSET, soulRatio, SOUL_BAR_BG_SPRITE, SOUL_BAR_PROGRESS_SPRITE);
    }

    private void drawHorizontalGaugeSprite(@NotNull GuiGraphicsExtractor graphics, int x, int y, float ratio,
                                           @NotNull Identifier bgSprite, @NotNull Identifier progressSprite) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bgSprite, x, y, GAUGE_BAR_WIDTH, GAUGE_BAR_HEIGHT);

        int filledWidth = (int) (GAUGE_BAR_WIDTH * ratio);
        if (filledWidth > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, progressSprite,
                    GAUGE_BAR_WIDTH, GAUGE_BAR_HEIGHT,
                    0, 0,
                    x, y,
                    filledWidth, GAUGE_BAR_HEIGHT);
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}