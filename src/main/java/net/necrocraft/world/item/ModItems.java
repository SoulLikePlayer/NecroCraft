package net.necrocraft.world.item;

import net.minecraft.world.item.*;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.food.ModFoods;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.impl.*;
import net.necrocraft.world.item.bonus.impl.synced.StrayCapeBonusItem;
import net.necrocraft.world.item.bonus.impl.synced.ZombieNautillusShellBonusItem;
import net.necrocraft.world.item.bonus.impl.synced.MummyWrappingBonusItem;
import net.necrocraft.world.item.bonus.impl.synced.TridentShardBonusItem;
import net.necrocraft.world.item.component.ModConsumables;
import net.necrocraft.world.item.equipment.bone.classic.BoneAxe;
import net.necrocraft.world.item.equipment.bone.classic.BoneEquipment;
import net.necrocraft.world.item.equipment.RevocationScepter;
import net.necrocraft.world.item.equipment.SoulTotem;
import net.necrocraft.world.item.equipment.bone.classic.BoneHoe;
import net.necrocraft.world.item.equipment.bone.classic.BoneShovel;
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

    public static DeferredItem<@NotNull Item> REVOCATION_SCEPTER =ITEMS.registerItem(
            "revocation_scepter",
            RevocationScepter::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull Item> NECROTIC_APPLE = ITEMS.registerItem(
            "necrotic_apple",
            Item::new,
            properties -> properties
                    .food(ModFoods.NECROTIC_APPLE, ModConsumables.NECROTIC_APPLE)
    );

    public static DeferredItem<@NotNull Item> BONE_SWORD = ITEMS.registerItem(
            "bone_sword",
            BoneEquipment::new,
            properties -> properties
                    .sword(ModToolMaterial.BONE, 3.0F, -2.4F)
    );

    public static DeferredItem<@NotNull Item> BONE_SHOVEL = ITEMS.registerItem(
            "bone_shovel",
            BoneShovel::new,
            properties -> properties
                    .shovel(ModToolMaterial.BONE, 1.5f, -3.0f)
    );

    public static DeferredItem<@NotNull Item> BONE_PICKAXE = ITEMS.registerItem(
            "bone_pickaxe",
            BoneEquipment::new,
            properties -> properties
                    .pickaxe(ModToolMaterial.BONE, 1.0f, -2.8f)
    );

    public static DeferredItem<@NotNull Item> BONE_AXE = ITEMS.registerItem(
            "bone_axe",
            BoneAxe::new,
            properties -> properties
                    .pickaxe(ModToolMaterial.BONE, 7.0f, -3.2f)
    );

    public static DeferredItem<@NotNull Item> BONE_HOE = ITEMS.registerItem(
            "bone_hoe",
            BoneHoe::new,
            properties -> properties
                    .hoe(ModToolMaterial.BONE, 7.0f, -3.2f)
    );

    public static DeferredItem<@NotNull Item> NETHERIFIED_BONE_SWORD = ITEMS.registerItem(
            "netherified_bone_sword",
            BoneEquipment::new,
            properties -> properties
                    .sword(ModToolMaterial.NETHERIFIED_BONE, 3.0F, -2.4F)
                    .fireResistant()
    );

    public static DeferredItem<@NotNull AbstractBonusItem> SOLID_SKIN_BONUS_ITEM = ITEMS.registerItem(
            "solid_skin_bonus_item",
            SolidSkinBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> HEALTH_BONUS_ITEM = ITEMS.registerItem(
            "health_bonus_item",
            HealthBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> MUMMY_WRAPPING_BONUS_ITEM = ITEMS.registerItem(
            "mummy_wrapping_bonus_item",
            MummyWrappingBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
    );

    public static DeferredItem<@NotNull AbstractBonusItem> TRIDENT_SHARD_BONUS_ITEM = ITEMS.registerItem(
            "trident_shard_bonus_item",
            TridentShardBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
    );

    public static DeferredItem<@NotNull AbstractBonusItem> ZOMBIE_NAUTILUS_SHELL_BONUS_ITEM = ITEMS.registerItem(
            "zombie_nautilus_shell",
            ZombieNautillusShellBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
    );

    public static DeferredItem<@NotNull AbstractBonusItem> STRAY_CAPE_BONUS_ITEM = ITEMS.registerItem(
            "stray_cape_bonus_item",
            StrayCapeBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
    );

    public static DeferredItem<@NotNull AbstractBonusItem> POST_MORTEM_EXPLOSION_BONUS_ITEM = ITEMS.registerItem(
            "post_mortem_explosion_bonus_item",
            PostMortemExplosionBonusItem::new,
            properties -> properties
                    .rarity(Rarity.RARE)
    );

    public static DeferredItem<@NotNull AbstractBonusItem> STORAGE_BONUS_ITEM = ITEMS.registerItem(
            "storage_bonus_item",
            StorageBonusItem::new,
            properties -> properties
    );


    public static DeferredItem<@NotNull AbstractBonusItem> HUNTER_BONUS_ITEM = ITEMS.registerItem(
            "hunter_bonus_item",
            HunterBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> FARMER_BONUS_ITEM = ITEMS.registerItem(
            "farmer_bonus_item",
            FarmerBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> AUTO_PLANTER_BONUS_ITEM = ITEMS.registerItem(
            "auto_planter_bonus_item",
            AutoPlanterBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> AUTO_TILLER_BONUS_ITEM = ITEMS.registerItem(
            "auto_tiller_bonus_item",
            AutoTillerBonusItem::new,
            properties -> properties
    );

    public static DeferredItem<@NotNull AbstractBonusItem> TRIAL_VAULT_BONUS_ITEM = ITEMS.registerItem(
            "trial_vault_bonus_item",
            TrialVaultBonusItem::new,
            properties -> properties
                    .rarity(Rarity.RARE)
    );

    public static DeferredItem<@NotNull BlockItem> SOUL_CARVING_TABLE = ITEMS.registerSimpleBlockItem(
            ModBlock.SOUL_CARVING_TABLE_BLOCK
    );
}
