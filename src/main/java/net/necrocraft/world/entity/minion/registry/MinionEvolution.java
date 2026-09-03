package net.necrocraft.world.entity.minion.registry;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

public record MinionEvolution(@NotNull EntityType<?> result,
                              @NotNull Item ingredient,
                              int ingredientCount) {

    public boolean acceptsIngredient(@NotNull ItemStack stack){
        return stack.is(this.ingredient) && stack.getCount() >= ingredientCount;
    }
}
