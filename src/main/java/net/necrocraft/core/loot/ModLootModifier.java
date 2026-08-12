package net.necrocraft.core.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModLootModifier {
    public static final DeferredRegister<@NotNull MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NecroCraft.MODID);

    public static final Supplier<MapCodec<MummyWrappingLootModifier>> MUMMY_WRAPPING_MODIFIER =
            LOOT_MODIFIERS.register("mummy_wrapping_modifier", () -> MummyWrappingLootModifier.CODEC);

    public static final Supplier<MapCodec<TridentShardLootModifier>> TRIDENT_SHARD_MODIFIER =
            LOOT_MODIFIERS.register("trident_shard_modifier", () -> TridentShardLootModifier.CODEC);

    public static final Supplier<MapCodec<TrialVaultLootModifier>> TRIAL_VAULT_MODIFIER =
            LOOT_MODIFIERS.register("trial_vault_modifier", () -> TrialVaultLootModifier.CODEC);
}