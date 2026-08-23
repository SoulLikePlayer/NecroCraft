package net.necrocraft.client;

import net.minecraft.client.Minecraft;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.network.SoulAbilityPayloads.RequestSoulEmpower;
import net.necrocraft.network.SoulAbilityPayloads.RequestSoulHeal;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = NecroCraft.MODID, value = Dist.CLIENT)
public final class SoulAbilityKeyHandler {

    private SoulAbilityKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        while (ModKeyMappings.SOUL_HEAL.consumeClick()) {
            ClientPacketDistributor.sendToServer(new RequestSoulHeal());
        }

        while (ModKeyMappings.SOUL_EMPOWER.consumeClick()) {
            ClientPacketDistributor.sendToServer(new RequestSoulEmpower());
        }
    }
}