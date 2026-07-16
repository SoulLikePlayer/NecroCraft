package net.necrocraft.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.equipment.SoulTotem;
import net.necrocraft.world.level.block.ModBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(NecroCraft.MODID);

    public static final DeferredItem<@NotNull Item> NECROTIC_SHARD = ITEMS.registerItem(
            "necrotic_shard",
            Item::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> SOUL_TOTEM = ITEMS.registerItem(
            "soul_totem",
            SoulTotem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull BlockItem> SOUL_CARVING_TABLE = ITEMS.registerSimpleBlockItem(ModBlock.SOUL_CARVING_TABLE_BLOCK);
}