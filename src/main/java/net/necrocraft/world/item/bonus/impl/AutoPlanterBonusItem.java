package net.necrocraft.world.item.bonus.impl;

import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class AutoPlanterBonusItem extends AbstractBonusItem {

    public AutoPlanterBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public boolean canAutoPlant() {
        return true;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
    }
}