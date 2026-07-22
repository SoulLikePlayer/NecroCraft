package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.necrocraft.client.NecroCraftClient;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import org.jetbrains.annotations.NotNull;

public class MinionInventoryScreen extends AbstractContainerScreen<@NotNull MinionInventoryMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "textures/gui/container/minion_inventory.png");

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
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}