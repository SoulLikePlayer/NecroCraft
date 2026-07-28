package net.necrocraft.world.item.bonus.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import static net.necrocraft.world.item.bonus.BonusUtil.applyModifier;
import static net.necrocraft.world.item.bonus.BonusUtil.removeModifier;

public class HealthBonusItem extends AbstractBonusItem {
    private static final Identifier HEALTH_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "health_bonus_item");

    private static final double HEALTH_MULTIPLIER = 1.0D;

    public HealthBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        applyModifier(minion, Attributes.MAX_HEALTH,
                HEALTH_MODIFIER_ID, HEALTH_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public void removeEffects(@NotNull AbstractMinion minion) {
        removeModifier(minion, Attributes.MAX_HEALTH, HEALTH_MODIFIER_ID);
    }
}