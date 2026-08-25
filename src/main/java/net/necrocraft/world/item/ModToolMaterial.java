package net.necrocraft.world.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;
import net.necrocraft.tags.ModItemTags;

public class ModToolMaterial {
    public static final ToolMaterial BONE;
    public static final ToolMaterial NETHERIFIED_BONE;

    static {
        BONE = new ToolMaterial(BlockTags.INCORRECT_FOR_COPPER_TOOL, 190, 5.0F, 1.0F, 13, ModItemTags.BONE_TOOL_MATERIALS);
        NETHERIFIED_BONE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
    }
}
