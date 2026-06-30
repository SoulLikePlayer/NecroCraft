package net.necrocraft.world.item;

import net.minecraft.world.item.Item;
import net.necrocraft.core.NecroCraft;
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
}