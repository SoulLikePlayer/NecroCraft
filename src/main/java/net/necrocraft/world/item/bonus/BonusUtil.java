package net.necrocraft.world.item.bonus;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class BonusUtil {
    public static Optional<AbstractBonusItem> resolve(Identifier id){
        return BuiltInRegistries.ITEM.get(id)
                .map(Holder.Reference::value)
                .filter(AbstractBonusItem.class::isInstance)
                .map(AbstractBonusItem.class::cast);
    }

    public static void applyModifier(AbstractMinion minion, Holder<@NotNull Attribute> attribute, Identifier id,
                                     double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = minion.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, amount, operation));
    }

    public static void removeModifier(AbstractMinion minion, Holder<@NotNull Attribute> attribute, @Nullable Identifier id) {
        AttributeInstance instance = minion.getAttribute(attribute);
        if (instance != null && id != null) {
            instance.removeModifier(id);
        }
    }
}
