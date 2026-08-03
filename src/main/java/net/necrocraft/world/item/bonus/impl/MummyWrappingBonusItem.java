package net.necrocraft.world.item.bonus.impl;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class MummyWrappingBonusItem extends AbstractBonusItem {

    private static final float HEAL_AMOUNT_ON_HIT = 1.0F;

    public MummyWrappingBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        minion.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, true, true));
        NecroCraft.LOGGER.info("Regeneration");
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        minion.heal(HEAL_AMOUNT_ON_HIT);
        NecroCraft.LOGGER.info("Healed");
    }

    public BonusTrigger getBonusTrigger(){
        return BonusTrigger.ON_DAMAGE;
    }
}
