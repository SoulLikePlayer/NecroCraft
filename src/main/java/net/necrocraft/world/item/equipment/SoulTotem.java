package net.necrocraft.world.item.equipment;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.entity.minion.MinionRegistry;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.component.SoulData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SoulTotem extends Item {
    public SoulTotem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SoulData soulData = stack.get(ModDataComponents.SOUL_DATA.get());

        if (soulData == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Optional<Holder.Reference<@NotNull EntityType<?>>> capturedType = BuiltInRegistries.ENTITY_TYPE.get(soulData.entityType());
        EntityType<? extends @NotNull AbstractMinion> minionType = MinionRegistry.getMinionFor(capturedType);
        if (minionType == null) {
            return InteractionResult.FAIL;
        }

        AbstractMinion minion = minionType.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (minion == null) {
            return InteractionResult.FAIL;
        }

        minion.setOwner(player);
        minion.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        ((ServerLevel) level).addFreshEntity(minion);

        return InteractionResult.SUCCESS;
    }
}
