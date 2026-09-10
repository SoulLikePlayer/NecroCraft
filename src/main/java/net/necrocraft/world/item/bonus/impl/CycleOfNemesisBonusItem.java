package net.necrocraft.world.item.bonus.impl;

import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class CycleOfNemesisBonusItem extends AbstractBonusItem {
    public CycleOfNemesisBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.POST_MORTEM;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_DEATH;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        minion.setHealth(minion.getMaxHealth());
        minion.setNemesis(true);
        minion.removeBonus(ModItems.CYCLE_OF_NEMESIS_BONUS_ITEM.getId());
    }
}
