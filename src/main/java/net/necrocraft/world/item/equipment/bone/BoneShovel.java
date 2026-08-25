package net.necrocraft.world.item.equipment.bone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

public class BoneShovel extends BoneEquipment{
    protected static final HashMap FLATTENABLES;

    public BoneShovel(Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        } else {
            Player player = context.getPlayer();
            BlockState newState = getShovelPathingState(blockState);
            if (newState == null) {
                newState = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
            }
            BlockState updatedState = null;
            if (newState != null && level.getBlockState(pos.above()).isAir()) {
                level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                updatedState = newState;
            } else if ((updatedState = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_DOUSE, false)) != null && !level.isClientSide()) {
                level.levelEvent((Entity)null, 1009, pos, 0);
            }

            if (updatedState != null) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, updatedState, 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, updatedState));
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
                    }
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public static @Nullable BlockState getShovelPathingState(BlockState originalState) {
        return (BlockState)FLATTENABLES.get(originalState.getBlock());
    }

    public boolean canPerformAction(@NotNull ItemInstance stack, @NotNull ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
    }

    static {
        FLATTENABLES = Maps.newHashMap(
                (new ImmutableMap.Builder()).put(Blocks.GRASS_BLOCK, Blocks.SOUL_SAND.defaultBlockState())
                        .put(Blocks.DIRT, Blocks.SOUL_SAND.defaultBlockState())
                        .put(Blocks.PODZOL, Blocks.SOUL_SAND.defaultBlockState())
                        .put(Blocks.COARSE_DIRT, Blocks.SOUL_SAND.defaultBlockState())
                        .put(Blocks.MYCELIUM, Blocks.SOUL_SAND.defaultBlockState())
                        .put(Blocks.ROOTED_DIRT, Blocks.SOUL_SAND.defaultBlockState()).build());
    }
}