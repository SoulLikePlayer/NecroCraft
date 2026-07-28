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

/**
 * Shared, stateless helpers used by the farmer AI goals ({@link FarmCropsGoal},
 * {@link PlantSeedsGoal}, {@link TillFarmlandGoal}): the crop-to-seed lookup
 * table, hoe detection, and tillable-soil detection.
 */
public final class FarmingUtil {

    private static volatile Map<Block, Item> cropToSeed;

    /**
     * Returns a cached, immutable map from each registered {@link CropBlock}
     * to the seed item that plants it, built lazily on first access (and
     * rebuilt on demand after {@link #invalidateCache()}).
     *
     * @return an immutable map of crop blocks to their planting seed item
     */
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

    /**
     * Scans the item registry for {@link BlockItem}s whose block is a
     * {@link CropBlock} and builds a crop-to-seed mapping from them.
     *
     * @return a new immutable crop-to-seed map
     */
    private static Map<Block, Item> buildCropToSeedMap() {
        Map<Block, Item> map = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock cropBlock) {
                map.putIfAbsent(cropBlock, item);
            }
        }
        return Map.copyOf(map);
    }

    /**
     * Clears the cached crop-to-seed map, forcing it to be rebuilt on the
     * next call to {@link #cropToSeedMap()} (e.g. after registry changes).
     */
    public static synchronized void invalidateCache() {
        cropToSeed = null;
    }

    /**
     * @param stack the item stack to check
     * @return {@code true} if {@code stack} is tagged as a hoe
     */
    public static boolean isHoe(ItemStack stack) {
        return stack.is(ItemTags.HOES);
    }

    /**
     * @param state the block state to check
     * @return {@code true} if {@code state} is dirt, grass or a dirt path
     *         (i.e. a block that can be tilled into farmland)
     */
    public static boolean isTillableSoil(BlockState state) {
        if (state.is(BlockTags.DIRT)) {
            return true;
        }
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH;
    }

    private FarmingUtil() {}
}