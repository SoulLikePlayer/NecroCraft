package net.necrocraft.client;

import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.Identifier;
import net.necrocraft.client.gui.SoulGaugeOverlay;
import net.necrocraft.client.gui.screens.inventory.CarvingScreen;
import net.necrocraft.client.gui.screens.inventory.MinionInventoryScreen;
import net.necrocraft.client.particles.NemesisSoulParticle;
import net.necrocraft.client.renderer.entity.*;
import net.necrocraft.client.renderer.entity.state.SkeletonMinionRenderState;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.inventory.CarvingMenu;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import net.necrocraft.world.inventory.ModMenuTypes;
import net.necrocraft.world.level.block.SoulCarvingTableBlock;
import net.necrocraft.world.particle.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static net.necrocraft.world.entity.ModEntity.*;

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
        event.registerEntityRenderer(DROWNED_MINION.get(), DrownedMinionRenderer::new);
        event.registerEntityRenderer(STRAY_MINION.get(), StrayMinionRenderer::new);
        event.registerEntityRenderer(PARCHED_MINION.get(), ParchedMinionRenderer::new);
        event.registerEntityRenderer(BOGGED_MINION.get(), BoggedMinionRenderer::new);
        event.registerEntityRenderer(BLIGHTED_MINION.get(), BlightedMinionRenderer::new);
        event.registerEntityRenderer(HUSK_MINION.get(), HuskMinionRenderer::new);
        event.registerEntityRenderer(ARIDIFIED_MINION.get(), AridifiedMinionRenderer::new);
        event.registerEntityRenderer(FROSTY_MINION.get(), FrostyMinionRenderer::new);
        event.registerEntityRenderer(ECHOING_MINION.get(), EchoingMinionRenderer::new);

        event.registerEntityRenderer(ECHOING.get(), EchoingRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CARVING_MENU.get(), CarvingScreen::new);
        event.register(ModMenuTypes.MINION_INVENTORY.get(), MinionInventoryScreen::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event){
        Identifier soulGaugeId = Identifier.fromNamespaceAndPath(NecroCraft.MODID, "soul_gauge");
        SoulGaugeOverlay soulGauge = new SoulGaugeOverlay(net.minecraft.client.Minecraft.getInstance());

        event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR, soulGaugeId,
                (graphics, deltaTracker) -> {
                    soulGauge.extractBackground(graphics, deltaTracker);
                    soulGauge.extractRenderState(graphics, deltaTracker);
                });
    }

    @SubscribeEvent
    public static void registerProvider(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(ModParticles.NEMESIS_SOUL.get(), NemesisSoulParticle.Provider::new);
    }
}