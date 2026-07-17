package net.necrocraft.world.item.bonus;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class BonusUtil {
    public static Optional<AbstractBonusItem> resolve(Identifier id){
        return BuiltInRegistries.ITEM.get(id)
                .map(Holder.Reference::value)
                .filter(AbstractBonusItem.class::isInstance)
                .map(AbstractBonusItem.class::cast);
    }
}
