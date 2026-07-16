package net.necrocraft.world.level.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.necrocraft.core.NecroCraft;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModBlock {
    public static DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(NecroCraft.MODID);

    public static final DeferredBlock<@NotNull Block> SOUL_CARVING_TABLE_BLOCK = BLOCKS.registerBlock(
            "soul_carving_block",
            SoulCarvingTableBlock::new,
            props -> props
    );
}