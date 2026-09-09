package net.necrocraft.world.item;

import net.minecraft.world.item.*;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.food.ModFoods;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.impl.*;
import net.necrocraft.world.item.bonus.impl.synced.*;
import net.necrocraft.world.item.component.ModConsumables;
import net.necrocraft.world.item.equipment.NemesisShard;
import net.necrocraft.world.item.equipment.bone.classic.BoneAxe;
import net.necrocraft.world.item.equipment.bone.classic.BoneEquipment;
import net.necrocraft.world.item.equipment.ObedienceScepter;
import net.necrocraft.world.item.equipment.SoulTotem;
import net.necrocraft.world.item.equipment.bone.classic.BoneHoe;
import net.necrocraft.world.item.equipment.bone.classic.BoneShovel;
import net.necrocraft.world.item.equipment.bone.netherified.NetherifiedBoneAxe;
import net.necrocraft.world.item.equipment.bone.netherified.NetherifiedBoneEquipment;
import net.necrocraft.world.item.equipment.bone.netherified.NetherifiedBoneHoe;
import net.necrocraft.world.item.equipment.bone.netherified.NetherifiedBoneShovel;
import net.necrocraft.world.level.block.ModBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(NecroCraft.MODID);

    private static DeferredItem<@NotNull Item> register(String name, Function<Item.Properties, Item> factory) {
        return ITEMS.registerItem(name, factory, UnaryOperator.identity());
    }

    private static DeferredItem<@NotNull Item> register(
            String name, Function<Item.Properties, Item> factory, UnaryOperator<Item.Properties> props) {
        return ITEMS.registerItem(name, factory, props);
    }

    private static <T extends AbstractBonusItem> DeferredItem<@NotNull T> registerBonus(
            String name, Function<Item.Properties, T> factory) {
        return ITEMS.registerItem(name, factory, UnaryOperator.identity());
    }

    private static <T extends AbstractBonusItem> DeferredItem<@NotNull T> registerBonus(
            String name, Function<Item.Properties, T> factory, Rarity rarity) {
        return ITEMS.registerItem(name, factory, p -> p.rarity(rarity));
    }

    private static <T extends AbstractBonusItem> DeferredItem<@NotNull T> registerBonus(
            String name, Function<Item.Properties, T> factory, UnaryOperator<Item.Properties> props) {
        return ITEMS.registerItem(name, factory, props);
    }

    public static final DeferredItem<@NotNull Item> NECROTIC_SHARD =
            register("necrotic_shard", Item::new);

    public static final DeferredItem<@NotNull Item> NECROTIC_POWDER =
            register("necrotic_powder", Item::new);

    public static final DeferredItem<@NotNull Item> NEMESIS_SHARD =
            register("nemesis_shard", NemesisShard::new);

    public static final DeferredItem<@NotNull Item> SOUL_TOTEM =
            register("soul_totem", SoulTotem::new);

    public static final DeferredItem<@NotNull Item> OBEDIENCE_SCEPTER =
            register("obedience_scepter", ObedienceScepter::new);

    public static final DeferredItem<@NotNull Item> NECROTIC_APPLE = register(
            "necrotic_apple",
            Item::new,
            properties -> properties.food(ModFoods.NECROTIC_APPLE, ModConsumables.NECROTIC_APPLE)
    );

    public static final DeferredItem<@NotNull Item> BONE_SWORD = register(
            "bone_sword",
            BoneEquipment::new,
            properties -> properties.sword(ModToolMaterial.BONE, 3.0F, -2.4F)
    );

    public static final DeferredItem<@NotNull Item> BONE_SHOVEL = register(
            "bone_shovel",
            BoneShovel::new,
            properties -> properties.shovel(ModToolMaterial.BONE, 1.5f, -3.0f)
    );

    public static final DeferredItem<@NotNull Item> BONE_PICKAXE = register(
            "bone_pickaxe",
            BoneEquipment::new,
            properties -> properties.pickaxe(ModToolMaterial.BONE, 1.0f, -2.8f)
    );

       public static final DeferredItem<@NotNull Item> BONE_AXE = register(
            "bone_axe",
            BoneAxe::new,
            properties -> properties.axe(ModToolMaterial.BONE, 7.0f, -3.2f)
    );

    public static final DeferredItem<@NotNull Item> BONE_HOE = register(
            "bone_hoe",
            BoneHoe::new,
            properties -> properties.hoe(ModToolMaterial.BONE, 7.0f, -3.2f)
    );

    public static final DeferredItem<@NotNull Item> NETHERIFIED_BONE_SWORD = register(
            "netherified_bone_sword",
            NetherifiedBoneEquipment::new,
            properties -> properties
                    .sword(ModToolMaterial.NETHERIFIED_BONE, 3.0F, -2.4F)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull Item> NETHERIFIED_BONE_SHOVEL = register(
            "netherified_bone_shovel",
            NetherifiedBoneShovel::new,
            properties -> properties
                    .shovel(ModToolMaterial.NETHERIFIED_BONE, 1.5f, -3.0f)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull Item> NETHERIFIED_BONE_PICKAXE = register(
            "netherified_bone_pickaxe",
            NetherifiedBoneEquipment::new,
            properties -> properties
                    .pickaxe(ModToolMaterial.NETHERIFIED_BONE, 1.0f, -2.8f)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull Item> NETHERIFIED_BONE_AXE = register(
            "netherified_bone_axe",
            NetherifiedBoneAxe::new,
            properties -> properties
                    .axe(ModToolMaterial.NETHERIFIED_BONE, 7.0f, -3.2f)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull Item> NETHERIFIED_BONE_HOE = register(
            "netherified_bone_hoe",
            NetherifiedBoneHoe::new,
            properties -> properties
                    .hoe(ModToolMaterial.NETHERIFIED_BONE, 7.0f, -3.2f)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull Item> ECHOING_SPAWN_EGG = register(
            "echoing_spawn_egg",
            SpawnEggItem::new,
            properties -> properties
                    .spawnEgg(ModEntity.ECHOING.get())
    );

    public static final DeferredItem<@NotNull AbstractBonusItem> SOLID_SKIN_BONUS_ITEM =
            registerBonus("solid_skin_bonus_item", SolidSkinBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> HEALTH_BONUS_ITEM =
            registerBonus("health_bonus_item", HealthBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> POST_MORTEM_EXPLOSION_BONUS_ITEM =
            registerBonus("post_mortem_explosion_bonus_item", PostMortemExplosionBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> STORAGE_BONUS_ITEM =
            registerBonus("storage_bonus_item", StorageBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> HUNTER_BONUS_ITEM =
            registerBonus("hunter_bonus_item", HunterBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> FARMER_BONUS_ITEM =
            registerBonus("farmer_bonus_item", FarmerBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> AUTO_PLANTER_BONUS_ITEM =
            registerBonus("auto_planter_bonus_item", AutoPlanterBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> AUTO_TILLER_BONUS_ITEM =
            registerBonus("auto_tiller_bonus_item", AutoTillerBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> SOUL_GENERATOR_BONUS_ITEM =
            registerBonus("soul_generator_bonus_item", SoulGenerationBonusItem::new);

    public static final DeferredItem<@NotNull AbstractBonusItem> MUMMY_WRAPPING_BONUS_ITEM =
            registerBonus("mummy_wrapping_bonus_item", MummyWrappingBonusItem::new, Rarity.UNCOMMON);

    public static final DeferredItem<@NotNull AbstractBonusItem> TRIDENT_SHARD_BONUS_ITEM =
            registerBonus("trident_shard_bonus_item", TridentShardBonusItem::new, Rarity.UNCOMMON);

    public static final DeferredItem<@NotNull AbstractBonusItem> ZOMBIE_NAUTILUS_SHELL_BONUS_ITEM =
            registerBonus("zombie_nautilus_shell", ZombieNautillusShellBonusItem::new, Rarity.UNCOMMON);

    public static final DeferredItem<@NotNull AbstractBonusItem> STRAY_CAPE_BONUS_ITEM =
            registerBonus("stray_cape_bonus_item", StrayCapeBonusItem::new, Rarity.UNCOMMON);

    public static final DeferredItem<@NotNull AbstractBonusItem> CRACKED_HUSK_JAW_BONUS_ITEM =
            registerBonus("cracked_husk_jaw_bonus_item", CrackedHuskJawBonusItem::new, Rarity.UNCOMMON);

    public static final DeferredItem<@NotNull AbstractBonusItem> VIAL_PUTRID_VENOM_BONUS_ITEM = registerBonus(
            "vial_putrid_venom_bonus_item",
            VialPutridVenomBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
                    .food(ModFoods.PUTRID_VENOM_VIAL, ModConsumables.PUTRID_VENOM_VIAL)
    );

    public static final DeferredItem<@NotNull AbstractBonusItem> VIAL_WITHERING_VENOM_BONUS_ITEM = registerBonus(
            "vial_withering_venom_bonus_item",
            VialWitheringVenomBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
                    .food(ModFoods.PUTRID_VENOM_VIAL, ModConsumables.PUTRID_VENOM_VIAL)
    );

    public static final DeferredItem<@NotNull AbstractBonusItem> DEHYDRATED_ROTTEN_FLESH_BONUS_ITEM = registerBonus(
            "dehydrated_rotten_flesh_bonus_item",
            DehydratedRottenFleshBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
                    .food(ModFoods.DEHYDRATED_ROTTEN_FLESH, ModConsumables.DEHYDRATED_ROTTEN_FLESH)
    );

    public static final DeferredItem<@NotNull AbstractBonusItem> NETHERIFIED_BONE_BONUS_ITEM = registerBonus(
            "netherified_bone_bonus_item",
            NetherifiedBoneBonusItem::new,
            properties -> properties
                    .rarity(Rarity.UNCOMMON)
                    .fireResistant()
    );

    public static final DeferredItem<@NotNull AbstractBonusItem> TRIAL_VAULT_BONUS_ITEM =
            registerBonus("trial_vault_bonus_item", TrialVaultBonusItem::new, Rarity.RARE);

    public static final DeferredItem<@NotNull BlockItem> SOUL_CARVING_TABLE =
            ITEMS.registerSimpleBlockItem(ModBlock.SOUL_CARVING_TABLE_BLOCK);
}