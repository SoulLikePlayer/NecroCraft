package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import static net.necrocraft.world.item.bonus.BonusUtil.applyModifier;

public class TridentShardBonusItem extends AbstractBonusItem {
    private static final Identifier ATTACK_DAMAGE_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "attack_damage_modifier_id");

    private static final double ATTACK_DAMAGE_BONUS = 3.0D;

    public TridentShardBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        applyModifier(minion, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID,
                ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADD_VALUE);
    }
}
