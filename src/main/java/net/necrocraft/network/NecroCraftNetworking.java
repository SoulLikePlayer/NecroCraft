package net.necrocraft.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.network.SoulAbilityPayloads.RequestSoulEmpower;
import net.necrocraft.network.SoulAbilityPayloads.RequestSoulHeal;
import net.necrocraft.world.effect.SoulOfUndeadAbilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = NecroCraft.MODID)
public final class NecroCraftNetworking {

    private NecroCraftNetworking() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NecroCraft.MODID).versioned("1");

        registrar.playToServer(
                RequestSoulHeal.TYPE,
                RequestSoulHeal.STREAM_CODEC,
                handleOnServer((payload, player) -> SoulOfUndeadAbilities.handleSelfHealRequest(player))
        );

        registrar.playToServer(
                RequestSoulEmpower.TYPE,
                RequestSoulEmpower.STREAM_CODEC,
                handleOnServer((payload, player) -> SoulOfUndeadAbilities.handleEmpowerMinionsRequest(player))
        );
    }

    /**
     * Wraps a handler so the actual logic always runs on the server main
     * thread (via {@code enqueueWork}) rather than the network thread, since
     * it touches world state (entities, effects, attachments).
     */
    private static <T extends CustomPacketPayload> IPayloadHandler<@NotNull T> handleOnServer(java.util.function.BiConsumer<T, ServerPlayer> handler) {
        return (payload, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                handler.accept(payload, serverPlayer);
            }
        });
    }
}