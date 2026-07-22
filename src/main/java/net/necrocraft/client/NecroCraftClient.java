package net.necrocraft.client;

import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.necrocraft.client.gui.screens.inventory.CarvingScreen;
import net.necrocraft.client.gui.screens.inventory.MinionInventoryScreen;
import net.necrocraft.client.renderer.entity.SkeletonMinionRenderer;
import net.necrocraft.client.renderer.entity.ZombieMinionRenderer;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.inventory.CarvingMenu;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import net.necrocraft.world.inventory.ModMenuTypes;
import net.necrocraft.world.level.block.SoulCarvingTableBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static net.necrocraft.world.entity.ModEntity.SKELETON_MINION;
import static net.necrocraft.world.entity.ModEntity.ZOMBIE_MINION;

@Mod(value = NecroCraft.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = NecroCraft.MODID, value = Dist.CLIENT)
public class NecroCraftClient {
    public NecroCraftClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ZOMBIE_MINION.get(), ZombieMinionRenderer::new);
        event.registerEntityRenderer(SKELETON_MINION.get(), SkeletonMinionRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CARVING_MENU.get(), CarvingScreen::new);
        event.register(ModMenuTypes.MINION_INVENTORY.get(), MinionInventoryScreen::new);
    }
}