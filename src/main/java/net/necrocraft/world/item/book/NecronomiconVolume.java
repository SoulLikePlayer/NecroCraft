package net.necrocraft.world.item.book;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public enum NecronomiconVolume {
    VOLUME_1("volume_1", 2);

    public static final int MAX_PAGE_COUNT = 6;

    private final String id;
    private final int pageCount;
    private final Identifier[] pages = new Identifier[MAX_PAGE_COUNT];

    NecronomiconVolume(String id, int pageCount) {
        this.id = id;
        this.pageCount = pageCount;
        for (int i = 0; i < pageCount; i++) {
            this.pages[i] = Identifier.fromNamespaceAndPath(
                    "necrocraft", "textures/gui/necronomicon/" + id + "/page_" + (i + 1) + ".png");
        }
    }

    public Identifier getPageTexture(int page) {
        return this.pages[page];
    }

    public Component getTitle() {
        return Component.translatable("necronomicon.necrocraft." + this.id);
    }

    public int getPageCount(){
        return pageCount;
    }
}