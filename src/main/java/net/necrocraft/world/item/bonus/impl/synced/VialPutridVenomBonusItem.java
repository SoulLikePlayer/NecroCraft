package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VialPutridVenomBonusItem extends AbstractBonusItem {
    public VialPutridVenomBonusItem(Properties properties) {
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
        LivingEntity target = minion.getLastHurtMob();
        if(target != null){
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, true, true, true));
        }
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        LivingEntity target = minion.getLastHurtMob();
        if (target != null && target.hasEffect(MobEffects.POISON)) {
            MobEffectInstance instance = Objects.requireNonNull(target.getEffect(MobEffects.POISON));
            int actualAmplifier = instance.getAmplifier();
            int actualDuration = instance.getDuration();

            target.addEffect(new MobEffectInstance(MobEffects.POISON, actualDuration + 60, actualAmplifier + 1, true, true, true));
            minion.heal(1.0F);
        }
    }
}
