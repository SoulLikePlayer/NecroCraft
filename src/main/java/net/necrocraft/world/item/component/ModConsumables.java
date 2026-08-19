package net.necrocraft.world.item.component;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.necrocraft.world.effect.ModMobEffects;

import java.util.List;

import static net.minecraft.world.item.component.Consumables.defaultFood;

public class ModConsumables {
    public static final Consumable NECROTIC_APPLE;

    static {
        NECROTIC_APPLE = defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(
                List.of(new MobEffectInstance(ModMobEffects.SOUL_OF_UNDEAD, MobEffectInstance.INFINITE_DURATION)))).build();
    }
}
