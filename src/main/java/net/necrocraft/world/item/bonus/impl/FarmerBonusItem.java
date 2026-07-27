package net.necrocraft.world.item.bonus.impl;

import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class FarmerBonusItem extends AbstractBonusItem {

    public FarmerBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PROFESSION;
    }

    @Override
    public boolean isSedentary() {
        return true;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
    }
}