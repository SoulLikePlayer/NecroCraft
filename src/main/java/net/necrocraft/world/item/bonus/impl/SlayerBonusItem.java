package net.necrocraft.world.item.bonus.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class SlayerBonusItem extends AbstractBonusItem {

    private static final Set<EntityType<?>> HUNTABLE = BuiltInRegistries.ENTITY_TYPE.stream()
            .filter(type -> type.getCategory() == MobCategory.MONSTER)
            .collect(Collectors.toUnmodifiableSet());

    public SlayerBonusItem(Properties properties) {
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