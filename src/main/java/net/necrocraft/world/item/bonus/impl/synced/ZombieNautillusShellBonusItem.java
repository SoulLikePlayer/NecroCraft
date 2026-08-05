package net.necrocraft.world.item.bonus.impl.synced;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import static net.necrocraft.world.item.bonus.BonusUtil.applyModifier;

public class ZombieNautillusShellBonusItem extends AbstractBonusItem {
    private static final Identifier ARMOR_MODIFIER_ID =
            Identifier.fromNamespaceAndPath("necrocraft", "barnacle_shell_modifier");

    private static final double ARMOR_BONUS = 9.0D;

    public ZombieNautillusShellBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_HIT;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        applyModifier(minion, Attributes.ARMOR, ARMOR_MODIFIER_ID,
                ARMOR_BONUS, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void applySyncedEffect(@NotNull AbstractMinion minion) {
        NecroCraft.LOGGER.info("Resitance Added !");
        minion.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 0, true, true, true));
    }
}
