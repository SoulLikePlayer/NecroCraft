package net.necrocraft.world.item.bonus.impl;

import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class AutoTillerBonusItem extends AbstractBonusItem {

    public AutoTillerBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public boolean canAutoTill() {
        return true;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
    }
}