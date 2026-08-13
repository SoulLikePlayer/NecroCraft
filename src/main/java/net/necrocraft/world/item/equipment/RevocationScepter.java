package net.necrocraft.world.item.equipment;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RevocationScepter extends Item {
    public RevocationScepter(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand type) {
        if(target instanceof AbstractMinion minion){

            if(!Objects.requireNonNull(minion.getOwnerReference()).matches(player)) return InteractionResult.FAIL;

            minion.remove(Entity.RemovalReason.DISCARDED);

            return InteractionResult.PASS;
        }

        return super.interactLivingEntity(itemStack, player, target, type);
    }
}
