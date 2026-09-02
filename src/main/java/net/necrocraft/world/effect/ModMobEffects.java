package net.necrocraft.world.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.curse.CurseOfTheSea;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModMobEffects {
    public static final DeferredRegister<@NotNull MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, NecroCraft.MODID);

    public static final Holder<@NotNull MobEffect> CURSE_OF_THE_SEA = MOB_EFFECTS.register("curse_of_the_sea", () -> new CurseOfTheSea(
            MobEffectCategory.BENEFICIAL,
            0xADD8E6
    ));




    public static final Holder<@NotNull MobEffect> SOUL_OF_UNDEAD = MOB_EFFECTS.register("soul_of_undead", () -> new SoulOfUndead(
            MobEffectCategory.NEUTRAL,
            0xADD8E6
    ));
}