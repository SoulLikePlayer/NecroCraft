package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import static net.necrocraft.world.item.bonus.BonusUtil.applyModifier;

public class StrayCapeBonusItem extends AbstractBonusItem {
    private static final Identifier SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "speed_modifier_id");

    private static final double SPEED_BONUS = 3.0D;
    private static final int COMBAT_COOLDOWN_TICKS = 24;

    public StrayCapeBonusItem(Properties properties) {
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
        applyModifier(minion, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, SPEED_BONUS, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        minion.setInvisible(conditionPassif(() ->  minion.tickCount - minion.lastCombatTick >= COMBAT_COOLDOWN_TICKS));
    }
}