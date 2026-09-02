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

public class DehydratedRottenFleshBonusItem extends AbstractBonusItem {
    public DehydratedRottenFleshBonusItem(Properties properties) {
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
        LivingEntity target = minion.getLastHurtMob();
        if(target != null){
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0, true, true, true));
        }
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getLastHurtMob();
        assert target != null;
        MobEffectInstance existingHunger = target.getEffect(MobEffects.HUNGER);
        int amplifier = existingHunger != null ? existingHunger.getAmplifier() : 0;

        double classicDamage = minion.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double bonusDamage = classicDamage * amplifier;
        if (bonusDamage > 0) {
            target.hurt(minion.damageSources().mobAttack(minion), (float) bonusDamage);
        }

        target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120, amplifier + 1, true, true, true));


    }
}
