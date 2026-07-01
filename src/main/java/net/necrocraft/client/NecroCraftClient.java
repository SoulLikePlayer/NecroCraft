package net.necrocraft.client;

import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.necrocraft.client.renderer.entity.SkeletonMinionRenderer;
import net.necrocraft.client.renderer.entity.ZombieMinionRenderer;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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
}