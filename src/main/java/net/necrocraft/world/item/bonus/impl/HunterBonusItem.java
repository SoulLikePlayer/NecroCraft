package net.necrocraft.world.item.bonus.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class HunterBonusItem extends AbstractBonusItem {

    private static final Set<EntityType<?>> HUNTABLE = Set.of(
            EntityTypes.PIG, EntityTypes.COW, EntityTypes.CHICKEN
    );

    public HunterBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PROFESSION;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
    }

    @Override
    public @NotNull Set<EntityType<?>> getHuntableTargets() {
        return HUNTABLE;
    }
}