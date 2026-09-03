package net.necrocraft.world.item.equipment;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.component.ObedienceActionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ObedienceScepter extends Item {
    public ObedienceScepter(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        ObedienceActionData current = stack.getOrDefault(
                ModDataComponents.OBEDIENCE_ACTION_DATA.get(),
                new ObedienceActionData(ObedienceAction.WANDERING)
        );
        ObedienceActionData next = current.next();
        stack.set(ModDataComponents.OBEDIENCE_ACTION_DATA.get(), next);

        level.playSound(null, player.blockPosition(),
                SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5F, 1.2F);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand type) {
        if(target instanceof AbstractMinion minion) {
            if (minion.getNemesis()) {
                return InteractionResult.FAIL;
            }

            if (!Objects.requireNonNull(minion.getOwnerReference()).matches(player))
                return InteractionResult.FAIL;

            ObedienceActionData data = itemStack.getOrDefault(
                    ModDataComponents.OBEDIENCE_ACTION_DATA.get(),
                    new ObedienceActionData(ObedienceAction.WANDERING)
            );

            switch (data.action()) {
                case REVOCATION -> {
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

                case SEDENTARY -> {
                    minion.setSedentary(!minion.getSedentary());
                    if (target.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, minion.blockPosition(),
                                SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                    return InteractionResult.SUCCESS;

                }

                case WANDERING -> {
                    minion.setWandering(!minion.getWandering());
                    if (target.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, minion.blockPosition(),
                                SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    }
                    return InteractionResult.SUCCESS;
                }
                default -> {
                    return InteractionResult.FAIL;
                }
            }
        }

        return super.interactLivingEntity(itemStack, player, target, type);
    }

    public enum ObedienceAction implements StringRepresentable {
        REVOCATION("revocation"),
        SEDENTARY("sedentary"),
        WANDERING("wandering");

        private final String name;

        public static final Codec<ObedienceAction> CODEC =
                StringRepresentable.fromEnum(ObedienceAction::values);

        public static final StreamCodec<@NotNull ByteBuf, @NotNull ObedienceAction> STREAM_CODEC =
                ByteBufCodecs.idMapper(
                        idx -> ObedienceAction.values()[idx],
                        ObedienceAction::ordinal
                );

        ObedienceAction(String name){
            this.name = name;
        }

        public ObedienceAction next() {
            ObedienceAction[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}