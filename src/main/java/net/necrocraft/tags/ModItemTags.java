package net.necrocraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

public class ModItemTags {
    public static final TagKey<@NotNull Item> BONE_TOOL_MATERIALS;
    public static final TagKey<@NotNull Item> NETHERIFIED_BONE_TOOL_MATERIALS;

    private static TagKey<@NotNull Item> register(String path){
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(NecroCraft.MODID, path));
    }

    static {
        BONE_TOOL_MATERIALS = register("bone_tool_materials");
        NETHERIFIED_BONE_TOOL_MATERIALS = register("netherified_bone_tool_materials");
    }
}
