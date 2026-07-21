package net.necrocraft.world.item.bonus.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SolidSkinBonusItem extends AbstractBonusItem {

    // Identifiants fixes pour retrouver/retirer les modifiers proprement
    private static final Identifier ARMOR_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_armor");
    private static final Identifier ARMOR_TOUGHNESS_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_armor_toughness");
    private static final Identifier SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "solid_skin_speed");

    // Valeurs de remplacement, ajustables. Equivalent visé:
    // - RESISTANCE amplifier 3 -> bonus d'armure/toughness conséquent
    // - SLOWNESS amplifier 1   -> malus de vitesse de ~30%
    private static final double ARMOR_BONUS = 12.0D;
    private static final double ARMOR_TOUGHNESS_BONUS = 6.0D;
    private static final double SPEED_MALUS_MULTIPLIER = -0.30D;

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
    }

    @Override
    public void removeEffects(@NotNull AbstractMinion minion) {
        removeModifier(minion, Attributes.ARMOR, ARMOR_MODIFIER_ID);
        removeModifier(minion, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MODIFIER_ID);
        removeModifier(minion, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID);
    }

    private void applyModifier(AbstractMinion minion, Holder<@NotNull Attribute> attribute, Identifier id,
                               double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = minion.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, amount, operation));
    }

    private void removeModifier(AbstractMinion minion, Holder<@NotNull Attribute> attribute, @Nullable Identifier id) {
        AttributeInstance instance = minion.getAttribute(attribute);
        if (instance != null && id != null) {
            instance.removeModifier(id);
        }
    }
}