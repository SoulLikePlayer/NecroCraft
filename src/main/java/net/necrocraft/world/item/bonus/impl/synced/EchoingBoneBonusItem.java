package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class EchoingBoneBonusItem extends AbstractBonusItem {
    public EchoingBoneBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_DAMAGE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getTarget();
        if(target != null){
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, true, true, true));
        }
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getTarget();
        assert target != null;
        MobEffectInstance existingDarkness = target.getEffect(MobEffects.DARKNESS);
        int amplifier = existingDarkness != null ? existingDarkness.getAmplifier() : 0;

        double classicDamage = minion.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double bonusDamage = classicDamage * amplifier;
        if (bonusDamage > 0) {
            target.hurt(minion.damageSources().mobAttack(minion), (float) bonusDamage);
        }

        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, amplifier + 1, true, true, true));
    }
}
