package net.necrocraft.world.item.equipment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

            if (minion.getNemesis()) {
                return InteractionResult.FAIL;
            }

            if(!Objects.requireNonNull(minion.getOwnerReference()).matches(player)) return InteractionResult.FAIL;

            if (target.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF,
                        minion.getX(), minion.getY() + minion.getBbHeight() / 2, minion.getZ(),
                        20, 0.3, 0.3, 0.3, 0.02);
                serverLevel.playSound(null, minion.blockPosition(),
                        SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            minion.dropItems();
            minion.remove(Entity.RemovalReason.DISCARDED);

            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, target, type);
    }
}