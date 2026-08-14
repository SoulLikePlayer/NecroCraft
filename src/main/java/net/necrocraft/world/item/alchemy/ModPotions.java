package net.necrocraft.world.item.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModPotions {
    public static final DeferredRegister<@NotNull Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, NecroCraft.MODID);

    public static final Holder<@NotNull Potion> POTION_OF_UNDEAD = POTIONS.register("potion_of_undeand", registryName -> new Potion(
            registryName.getPath(),
            new MobEffectInstance(ModMobEffects.SOUL_OF_UNDEAD, 3600)
    ));
}
