package net.necrocraft.client;

import net.minecraft.client.Minecraft;
import net.necrocraft.client.gui.screens.inventory.NecronomiconScreen;
import net.necrocraft.world.item.book.NecronomiconVolume;

public final class NecronomiconClientHooks {
    private NecronomiconClientHooks() {}

    public static void openBook(NecronomiconVolume volume) {
        Minecraft.getInstance().setScreenAndShow(new NecronomiconScreen(volume));
    }
}