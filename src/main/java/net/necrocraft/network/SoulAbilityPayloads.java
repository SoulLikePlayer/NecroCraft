package net.necrocraft.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

public final class SoulAbilityPayloads {

    private SoulAbilityPayloads() {
    }

    /**
     * Request to trigger the "self heal" Soul of Undead ability.
     */
    public record RequestSoulHeal() implements CustomPacketPayload {
        public static final Type<RequestSoulHeal> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(NecroCraft.MODID, "request_soul_heal"));

        public static final StreamCodec<RegistryFriendlyByteBuf, RequestSoulHeal> STREAM_CODEC =
                StreamCodec.unit(new RequestSoulHeal());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Request to trigger the "empower nearby minions" (Speed + Strength) Soul of Undead ability.
     */
    public record RequestSoulEmpower() implements CustomPacketPayload {
        public static final Type<@NotNull RequestSoulEmpower> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(NecroCraft.MODID, "request_soul_empower"));

        public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull RequestSoulEmpower> STREAM_CODEC =
                StreamCodec.unit(new RequestSoulEmpower());

        @Override
        public Type<@NotNull ? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}