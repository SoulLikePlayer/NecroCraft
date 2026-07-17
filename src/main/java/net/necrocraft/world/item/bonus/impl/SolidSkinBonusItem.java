package net.necrocraft.world.item.bonus.impl;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class SolidSkinBonusItem extends AbstractBonusItem {
    private static final  int RESISTANCE_AMPLIFIER = 3;
    private static final int SLOWNESS_AMPLIFIER = 1;

    public SolidSkinBonusItem(Properties properties){
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        minion.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,
                MobEffectInstance.INFINITE_DURATION, RESISTANCE_AMPLIFIER, false, false, true));
        minion.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                MobEffectInstance.INFINITE_DURATION, RESISTANCE_AMPLIFIER, false, false, true));

        minion.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                MobEffectInstance.INFINITE_DURATION, SLOWNESS_AMPLIFIER, false, false, true));
    }
}
