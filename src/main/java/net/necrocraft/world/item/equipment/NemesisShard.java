package net.necrocraft.world.item.equipment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.util.AdvancementUtil;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.particle.ModParticles;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Shard of concentrated nemesis magic. Used on an owned minion, it permanently
 * corrupts it: the minion goes feral, attacking anything nearby (including
 * its former owner, see {@link net.necrocraft.world.entity.ai.goal.NemesisHurtTargetGoal}),
 * refuses to open its inventory, and can no longer be revoked with a
 * {@link RevocationScepter}. There is no known way back — corrupting a minion
 * is a deliberate, irreversible choice.
 */
public class NemesisShard extends Item {
    public NemesisShard(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack,
                                                           @NotNull Player player,
                                                           @NotNull LivingEntity target,
                                                           @NotNull InteractionHand type) {

        if (target instanceof AbstractMinion minion) {
            if (minion.getNemesis()) return InteractionResult.PASS;
            if (!(Objects.equals(minion.getOwner(), player.getLivingEntity()))) return InteractionResult.PASS;

            if (!player.level().isClientSide()) {

                minion.setNemesis(true);

                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ModParticles.NEMESIS_SOUL.get(),
                            minion.getX(), minion.getY() + minion.getBbHeight() * 0.5, minion.getZ(),
                            20,
                            0.3, 0.3, 0.3,
                            0.02
                    );
                }

                itemStack.shrink(1);
                AdvancementUtil.grant((ServerPlayer) player, "ignore_the_warning");
            }

            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(itemStack, player, target, type);
    }
}