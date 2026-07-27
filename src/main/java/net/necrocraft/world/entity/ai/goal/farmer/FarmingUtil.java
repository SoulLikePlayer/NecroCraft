package net.necrocraft.world.entity.ai.goal.farmer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class FarmingUtil {

    private static volatile Map<Block, Item> cropToSeed;

    public static Map<Block, Item> cropToSeedMap() {
        Map<Block, Item> map = cropToSeed;
        if (map == null) {
            synchronized (FarmingUtil.class) {
                map = cropToSeed;
                if (map == null) {
                    map = buildCropToSeedMap();
                    cropToSeed = map;
                }
            }
        }
        return map;
    }

    private static Map<Block, Item> buildCropToSeedMap() {
        Map<Block, Item> map = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock cropBlock) {
                map.putIfAbsent(cropBlock, item);
            }
        }
        return Map.copyOf(map);
    }

    public static synchronized void invalidateCache() {
        cropToSeed = null;
    }
    public static boolean isHoe(ItemStack stack) {
        return stack.is(ItemTags.HOES);
    }

    public static boolean isTillableSoil(BlockState state) {
        if (state.is(BlockTags.DIRT)) {
            return true;
        }
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH;
    }

    private FarmingUtil() {}
}