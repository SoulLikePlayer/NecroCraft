package net.necrocraft.world.item.bonus.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import static net.necrocraft.world.item.bonus.BonusUtil.applyModifier;
import static net.necrocraft.world.item.bonus.BonusUtil.removeModifier;

public class SolidSkinBonusItem extends AbstractBonusItem {
    private static final Identifier ARMOR_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_armor");
    private static final Identifier ARMOR_TOUGHNESS_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_armor_toughness");
    private static final Identifier SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_speed");
    private static final Identifier KNOCKBACK_RESISTANCE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_knockback_resistance");
    private static final Identifier EXPLOSION_KNOCKBACK_RESISTANCE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_explosion_knockback_resistance");


    private static final double ARMOR_BONUS = 18.0D;
    private static final double ARMOR_TOUGHNESS_BONUS = 9.0D;
    private static final double SPEED_MALUS_MULTIPLIER = -0.30D;

    private static final double RESISTANCE_BONUS = 0.30D;

    public SolidSkinBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        applyModifier(minion, Attributes.ARMOR, ARMOR_MODIFIER_ID,
                ARMOR_BONUS, AttributeModifier.Operation.ADD_VALUE);
        applyModifier(minion, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MODIFIER_ID,
                ARMOR_TOUGHNESS_BONUS, AttributeModifier.Operation.ADD_VALUE);
        applyModifier(minion, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID,
                SPEED_MALUS_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        applyModifier(minion, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID,
                RESISTANCE_BONUS, AttributeModifier.Operation.ADD_VALUE);
        applyModifier(minion, Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, EXPLOSION_KNOCKBACK_RESISTANCE_MODIFIER_ID,
                RESISTANCE_BONUS, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void removeEffects(@NotNull AbstractMinion minion) {
        removeModifier(minion, Attributes.ARMOR, ARMOR_MODIFIER_ID);
        removeModifier(minion, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MODIFIER_ID);
        removeModifier(minion, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID);
        removeModifier(minion, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID);
        removeModifier(minion, Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, EXPLOSION_KNOCKBACK_RESISTANCE_MODIFIER_ID);

        minion.removeEffect(MobEffects.RESISTANCE);
    }
}