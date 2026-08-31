package net.necrocraft.world.item.bonus.impl;

import net.necrocraft.core.ModAttachments;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

public class SoulGenerationBonusItem extends AbstractBonusItem {
    public SoulGenerationBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_TICK;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        if(minion.tickCount % 60 == 0){
            minion.setData(ModAttachments.SOUL_GAUGE, Math.min(100, minion.getExistingData(ModAttachments.SOUL_GAUGE).orElse(0f) + 5f));
        }
    }
}
