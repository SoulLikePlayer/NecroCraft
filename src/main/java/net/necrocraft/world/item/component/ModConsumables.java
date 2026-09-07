package net.necrocraft.world.item.component;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.necrocraft.world.effect.ModMobEffects;

import java.util.List;

import static net.minecraft.world.item.component.Consumables.defaultDrink;
import static net.minecraft.world.item.component.Consumables.defaultFood;

public class ModConsumables {
    public static final Consumable NECROTIC_APPLE;
    public static final Consumable PUTRID_VENOM_VIAL;
    public static final Consumable DEHYDRATED_ROTTEN_FLESH;

    static {
        NECROTIC_APPLE = defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(
                List.of(new MobEffectInstance(ModMobEffects.SOUL_OF_UNDEAD, MobEffectInstance.INFINITE_DURATION)))).build();

        PUTRID_VENOM_VIAL = defaultDrink().onConsume(new ApplyStatusEffectsConsumeEffect(
                List.of(new MobEffectInstance(MobEffects.POISON, 100, 1),
                        new MobEffectInstance(MobEffects.HUNGER, 100, 1)))).build();

        DEHYDRATED_ROTTEN_FLESH = defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(
                List.of(new MobEffectInstance(MobEffects.HUNGER, 600, 1),
                        new MobEffectInstance(MobEffects.NAUSEA, 200, 0)))).build();

    }
}
