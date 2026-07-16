package net.necrocraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.necrocraft.world.inventory.CarvingMenu;
import org.jetbrains.annotations.NotNull;

public class SoulCarvingTableBlock extends Block {
    private static final MapCodec<SoulCarvingTableBlock> CODEC = simpleCodec(SoulCarvingTableBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.necrocraft.soul_carving_table");

    public @NotNull MapCodec<? extends SoulCarvingTableBlock> codec() {
        return CODEC;
    }

    public SoulCarvingTableBlock(Properties properties) {
        super(properties);
    }

    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
        }

        return InteractionResult.SUCCESS;
    }

    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, _) -> new CarvingMenu(containerId, inventory), CONTAINER_TITLE);
    }
}