package net.necrocraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.necrocraft.world.item.book.NecronomiconVolume;

public class NecronomiconScreen extends Screen {
    private static final int PAGE_TEX_SIZE = 256;
    private static final int PAGE_WIDTH = 192;
    private static final int PAGE_HEIGHT = 192;
    private static final int IMAGE_SIZE = 192;
    private static final int PAGE_BUTTON_Y = 157;
    private static final int PAGE_BACK_BUTTON_X = 43;
    private static final int PAGE_FORWARD_BUTTON_X = 116;

    private final NecronomiconVolume volume;
    private int currentPage;
    private PageButton forwardButton;
    private PageButton backButton;

    public NecronomiconScreen(NecronomiconVolume volume) {
        super(volume.getTitle());
        this.volume = volume;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .pos((this.width - 200) / 2, this.backgroundTop() + IMAGE_SIZE + 2)
                .width(200)
                .build());

        int left = this.backgroundLeft();
        int top = this.backgroundTop();
        this.forwardButton = this.addRenderableWidget(
                new PageButton(left + PAGE_FORWARD_BUTTON_X, top + PAGE_BUTTON_Y, true, b -> this.pageForward(), true));
        this.backButton = this.addRenderableWidget(
                new PageButton(left + PAGE_BACK_BUTTON_X, top + PAGE_BUTTON_Y, false, b -> this.pageBack(), true));
        this.updateButtonVisibility();
    }

    private void pageBack() {
        if (this.currentPage > 0) this.currentPage--;
        this.updateButtonVisibility();
    }

    private void pageForward() {
        if (this.currentPage < this.volume.getPageCount() - 1) this.currentPage++;
        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = this.currentPage < this.volume.getPageCount() - 1;
        this.backButton.visible = this.currentPage > 0;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) return true;
        return switch (event.key()) {
            case 266, 263 -> { this.backButton.onPress(event); yield true; }
            case 267, 262 -> { this.forwardButton.onPress(event); yield true; }
            default -> false;
        };
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.volume.getPageTexture(this.currentPage),
                this.backgroundLeft(), this.backgroundTop(),
                0.0F, 0.0F,
                PAGE_WIDTH, PAGE_HEIGHT,
                PAGE_TEX_SIZE, PAGE_TEX_SIZE);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private int backgroundLeft() {
        return (this.width - IMAGE_SIZE) / 2;
    }

    private int backgroundTop() {
        return 2;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}