package net.necrocraft.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.NecroCraft;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class ModItemGroups {
    public static DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NecroCraft.MODID);

    public static final Supplier<CreativeModeTab> NECROCRAFT_MATERIALS = register(
            "necrocraft_materials",
            ModItems.NECROTIC_SHARD,
            Component.translatable("itemGroup.necrocraft.materials"),
            List.of(
                    ModItems.NECROTIC_SHARD,
                    ModItems.NECROTIC_POWDER,
                    ModItems.NEMESIS_SHARD,
                    ModItems.NECROTIC_APPLE,

                    ModItems.SOUL_CARVING_TABLE
            )
    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_EQUIPMENT = register(
            "necrocraft_equipment",
            ModItems.SOUL_TOTEM,
            Component.translatable("itemGroup.necrocraft.equipment"),
            List.of(
                    ModItems.SOUL_TOTEM,
                    ModItems.OBEDIENCE_SCEPTER,

                    ModItems.BONE_SWORD,
                    ModItems.BONE_SHOVEL,
                    ModItems.BONE_PICKAXE,
                    ModItems.BONE_AXE,
                    ModItems.BONE_HOE,

                    ModItems.NETHERIFIED_BONE_SWORD,
                    ModItems.NETHERIFIED_BONE_SHOVEL,
                    ModItems.NETHERIFIED_BONE_PICKAXE,
                    ModItems.NETHERIFIED_BONE_AXE,
                    ModItems.NETHERIFIED_BONE_HOE
            )
    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_BONUS = register(
            "necrocraft_bonus",
            ModItems.SOLID_SKIN_BONUS_ITEM,
            Component.translatable("itemGroup.necrocraft.bonus"),
            List.of(
                    ModItems.SOLID_SKIN_BONUS_ITEM,
                    ModItems.POST_MORTEM_EXPLOSION_BONUS_ITEM,
                    ModItems.NETHERIFIED_BONE_BONUS_ITEM,
                    ModItems.SOUL_GENERATOR_BONUS_ITEM,

                    ModItems.STORAGE_BONUS_ITEM,
                    ModItems.TRIAL_VAULT_BONUS_ITEM,
                    ModItems.CYCLE_OF_NEMESIS_BONUS_ITEM,

                    ModItems.HUNTER_BONUS_ITEM,
                    ModItems.FARMER_BONUS_ITEM,
                    ModItems.AUTO_PLANTER_BONUS_ITEM,
                    ModItems.AUTO_TILLER_BONUS_ITEM,

                    ModItems.MUMMY_WRAPPING_BONUS_ITEM,

                    ModItems.DEHYDRATED_ROTTEN_FLESH_BONUS_ITEM,
                    ModItems.CRACKED_HUSK_JAW_BONUS_ITEM,

                    ModItems.TRIDENT_SHARD_BONUS_ITEM,
                    ModItems.ZOMBIE_NAUTILUS_SHELL_BONUS_ITEM,

                    ModItems.STRAY_CAPE_BONUS_ITEM,
                    ModItems.FROSTY_BONE_BONUS_ITEM,

                    ModItems.POISONED_BONE_BONUS_ITEM,
                    ModItems.WITHERED_BONE_BONUS_ITEM,
                    ModItems.ECHOING_BONE_BONUS_ITEM
            )
    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_SPAWN_EGG = register(
            "necrocraft_spawn_egg",
            ModItems.ECHOING_SPAWN_EGG,
            Component.translatable("itemGroup.necrocraft.spawn_egg"),
            List.of(
                    ModItems.ECHOING_SPAWN_EGG
            )
    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_NECRONOMICON = register(
            "necrocraft_necronomicon",
            ModItems.NECRONOMICON_1,
            Component.translatable("itemGroup.necrocraft.necronomicon"),
            List.of(
                    ModItems.NECRONOMICON_1
            )
    );


    private static Supplier<CreativeModeTab> register(String name, DeferredItem<? extends @NotNull Item> icon, Component title, List<DeferredItem<? extends @NotNull Item>> items) {
        return CREATIVE_MODE_TABS.register(name,
                () -> CreativeModeTab.builder()
                        .icon(() -> new ItemStack(icon.get()))
                        .title(title)
                        .displayItems(((_, output) -> {
                            for (DeferredItem<? extends @NotNull Item> itemOutput : items) {
                                output.accept(itemOutput);
                            }
                        }))
                        .build());
    }
}