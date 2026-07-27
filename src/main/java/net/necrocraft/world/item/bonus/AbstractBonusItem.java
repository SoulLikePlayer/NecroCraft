package net.necrocraft.world.item.bonus;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.Set;

public abstract class AbstractBonusItem extends Item {
    protected AbstractBonusItem(Properties properties){
        super(properties);
    }

    public abstract @NotNull BonusType getBonusTypes();

    public abstract void applyEffectes(@NotNull AbstractMinion minion);

    public void removeEffects(@NotNull AbstractMinion minion) {
    }

    public @NotNull Set<EntityType<?>> getHuntableTargets() {
        return Set.of();
    }

    public boolean isSedentary() {
        return false;
    }

    public boolean canAutoPlant() {
        return false;
    }

    public boolean canAutoTill() {
        return false;
    }

    public void onDeath(@NotNull AbstractMinion minion, @NotNull ServerLevel level){}
}