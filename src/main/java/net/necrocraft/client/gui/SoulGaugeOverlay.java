package net.necrocraft.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

public class SoulGaugeOverlay implements ContextualBar {
    private static final Identifier SOUL_GAUGE_BACKGROUND_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "hud/soul_gauge_background");
    private static final Identifier SOUL_GAUGE_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(NecroCraft.MODID,"hud/soul_gauge_progress");

    private static final int BAR_WIDTH = 5;
    private static final int BAR_HEIGHT = 32;

    private static final int X_OFFSET_FROM_CENTER = 95;
    private static final int BOTTOM_MARGIN = 10;

    private final Minecraft minecraft;

    public SoulGaugeOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private boolean isVisible(LocalPlayer player) {
        if (this.minecraft.gameMode == null) return false;
        if (this.minecraft.gameMode.getPlayerMode() != GameType.SURVIVAL) return false;
        return player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
        LocalPlayer player = this.minecraft.player;
        if (player == null) return;
        if (!isVisible(player)) return;

        int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2 + X_OFFSET_FROM_CENTER;
        int y = screenHeight - BOTTOM_MARGIN - BAR_HEIGHT;

        float soulGauge = player.getData(ModAttachments.SOUL_GAUGE);
        float ratio = soulGauge / 100f;
        int filledHeight = (int) (ratio * BAR_HEIGHT);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SOUL_GAUGE_BACKGROUND_SPRITE, x, y, BAR_WIDTH, BAR_HEIGHT);

        if (filledHeight > 0) {
            int srcY = BAR_HEIGHT - filledHeight;
            int destY = y + srcY;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SOUL_GAUGE_PROGRESS_SPRITE,
                    BAR_WIDTH, BAR_HEIGHT,
                    0, srcY,
                    x, destY,
                    BAR_WIDTH, filledHeight);
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker) {
    }
}