package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.effect.ModMobEffects;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.entity.minion.impl.stray.FrostyMinion;
import net.necrocraft.world.entity.minion.impl.stray.StrayMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FrostyBoneBonusItem extends AbstractBonusItem {
    public FrostyBoneBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_DAMAGE;
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getTarget();
        if(target != null){
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, true, true, true));
        }
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getTarget();
        assert target != null;

        Holder<@NotNull MobEffect> mobEffect = switch (minion){
            case FrostyMinion _ -> ModMobEffects.CURSE_OF_THE_FROST;
            default -> MobEffects.SLOWNESS;
        };

        MobEffectInstance existingSlowness = target.getEffect(mobEffect);
        int amplifier = existingSlowness != null ? existingSlowness.getAmplifier() : 0;

        double classicDamage = minion.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double bonusDamage = classicDamage * amplifier;
        if (bonusDamage > 0) {
            target.hurt(minion.damageSources().mobAttack(minion), (float) bonusDamage);
        }

        target.addEffect(new MobEffectInstance(mobEffect, 120, amplifier + 1, true, true, true));
    }
}
