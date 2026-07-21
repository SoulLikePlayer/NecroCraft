package net.necrocraft.world.item.bonus;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public abstract class AbstractBonusItem extends Item {
    protected AbstractBonusItem(Properties properties){
        super(properties);
    }

    public abstract @NotNull BonusType getBonusTypes();

    public abstract void applyEffectes(@NotNull AbstractMinion minion);

    public void removeEffects(@NotNull AbstractMinion minion){}

    public void onDeath(@NotNull AbstractMinion minion, @NotNull ServerLevel level){}
}
