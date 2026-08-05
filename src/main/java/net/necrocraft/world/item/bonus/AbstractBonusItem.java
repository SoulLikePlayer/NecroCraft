package net.necrocraft.world.item.bonus;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractBonusItem extends Item {
    protected AbstractBonusItem(Properties properties){
        super(properties);
    }

    public static boolean conditionPassif(Supplier<Boolean> condition){
        return condition.get();
    }

    public abstract @NotNull BonusType getBonusTypes();

    /**
     * The single moment at which this bonus acts. {@link BonusTrigger#AUTO_PLANT}
     * and {@link BonusTrigger#AUTO_TILL} are continuous (checked every tick by
     * {@link AbstractMinion}'s goals) rather than one-shot, but they're
     * expressed through the same enum so every bonus declares "when" it does
     * something through this one method.
     */
    public @NotNull BonusTrigger getBonusTrigger(){
        return BonusTrigger.NONE;
    }

    /** Effect applied to every minion carrying this bonus when {@link #getBonusTrigger()} fires. */
    public abstract void applyEffectes(@NotNull AbstractMinion minion);

    /**
     * Additional effect applied on top of {@link #applyEffectes(AbstractMinion)},
     * but only for minion implementations that explicitly opt in (see
     * {@link AbstractMinion#getSyncedBonusItem()}). No-op by default: most
     * bonuses don't need a minion-specific variant.
     */
    public void applySyncedEffect(@NotNull AbstractMinion minion) {}

    public @NotNull Set<EntityType<?>> getHuntableTargets() {
        return Set.of();
    }

    public boolean isSedentary() {
        return false;
    }
}