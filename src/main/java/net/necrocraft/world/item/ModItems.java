package net.necrocraft.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.bonus.impl.*;
import net.necrocraft.world.item.equipment.SoulTotem;
import net.necrocraft.world.level.block.ModBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(NecroCraft.MODID);


    /**
     * Ingredient
     */
    public static final DeferredItem<@NotNull Item> NECROTIC_SHARD = ITEMS.registerItem(
            "necrotic_shard",
            Item::new,
            properties -> properties
    );

    public static final DeferredItem<@NotNull Item> NECROTIC_POWDER = ITEMS.registerItem(
            "necrotic_powder",
            Item::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> SOUL_TOTEM = ITEMS.registerItem(
            "soul_totem",
            SoulTotem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> SOLID_SKIN_BONUS_ITEM = ITEMS.registerItem(
            "solid_skin_bonus_item",
            SolidSkinBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> POST_MORTEM_EXPLOSION_BONUS_ITEM = ITEMS.registerItem(
            "post_mortem_explosion_bonus_item",
            PostMortemExplosionBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> STORAGE_BONUS_ITEM = ITEMS.registerItem(
            "storage_bonus_item",
            StorageBonusItem::new,
            properties -> properties
    );


    public static DeferredItem<@NotNull Item> HUNTER_BONUS_ITEM = ITEMS.registerItem(
            "hunter_bonus_item",
            HunterBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> FARMER_BONUS_ITEM = ITEMS.registerItem(
            "farmer_bonus_item",
            FarmerBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> AUTO_PLANTER_BONUS_ITEM = ITEMS.registerItem(
            "auto_planter_bonus_item",
            AutoPlanterBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> AUTO_TILLER_BONUS_ITEM = ITEMS.registerItem(
            "auto_tiller_bonus_item",
            AutoTillerBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull BlockItem> SOUL_CARVING_TABLE = ITEMS.registerSimpleBlockItem(
            ModBlock.SOUL_CARVING_TABLE_BLOCK
    );
}