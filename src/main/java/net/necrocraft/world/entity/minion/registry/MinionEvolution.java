package net.necrocraft.world.entity.minion.registry;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

public record MinionEvolution(@NotNull EntityType<?> result,
                              @NotNull Item ingredient,
                              int ingredientCount,
                              float soulAmount) {

    public boolean acceptsIngredient(@NotNull ItemStack stack){
        return stack.is(this.ingredient) && stack.getCount() >= ingredientCount;
    }

    public boolean acceptsAmount(@NotNull Player player){
        return player.getData(ModAttachments.SOUL_GAUGE) >= soulAmount;
    }
}
