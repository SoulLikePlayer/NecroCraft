package net.necrocraft.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.NecroCraft;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModItemGroups {
    public static DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NecroCraft.MODID);

    public static final Supplier<CreativeModeTab> NECROCRAFT_MATERIALS = CREATIVE_MODE_TABS.register("necrocraft_materials",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.NECROTIC_SHARD.get()))
                    .title(Component.translatable("itemGroup.necrocraft.materials"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.NECROTIC_SHARD);
                        output.accept(ModItems.NECROTIC_POWDER);
                        output.accept(ModItems.NEMESIS_SHARD);
                        output.accept(ModItems.NECROTIC_APPLE);

                        output.accept(ModItems.SOUL_CARVING_TABLE);
                    }))
                    .build()
    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_EQUIPMENT = CREATIVE_MODE_TABS.register("necrocraft_equipment",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SOUL_TOTEM.get()))
                    .title(Component.translatable("itemGroup.necrocraft.equipment"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SOUL_TOTEM);
                        output.accept(ModItems.REVOCATION_SCEPTER);

                        output.accept(ModItems.BONE_SWORD);
                        output.accept(ModItems.BONE_SHOVEL);
                        output.accept(ModItems.BONE_PICKAXE);
                        output.accept(ModItems.BONE_AXE);
                        output.accept(ModItems.BONE_HOE);

                        output.accept(ModItems.NETHERIFIED_BONE_SWORD);
                        output.accept(ModItems.NETHERIFIED_BONE_SHOVEL);
                        output.accept(ModItems.NETHERIFIED_BONE_PICKAXE);
                        output.accept(ModItems.NETHERIFIED_BONE_AXE);
                        output.accept(ModItems.NETHERIFIED_BONE_HOE);
                    }))
                    .build()
    );

   public static final Supplier<CreativeModeTab> NECROCRAFT_BONUS = CREATIVE_MODE_TABS.register("necrocraft_bonus",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SOLID_SKIN_BONUS_ITEM.get()))
                    .title(Component.translatable("itemGroup.necrocraft.bonus"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SOLID_SKIN_BONUS_ITEM);
                        output.accept(ModItems.HEALTH_BONUS_ITEM);
                        output.accept(ModItems.POST_MORTEM_EXPLOSION_BONUS_ITEM);
                        output.accept(ModItems.NETHERIFIED_BONE_BONUS_ITEM);
                        output.accept(ModItems.SOUL_GENERATOR_BONUS_ITEM);

                        output.accept(ModItems.STORAGE_BONUS_ITEM);
                        output.accept(ModItems.TRIAL_VAULT_BONUS_ITEM);

                        output.accept(ModItems.HUNTER_BONUS_ITEM);
                        output.accept(ModItems.FARMER_BONUS_ITEM);
                        output.accept(ModItems.AUTO_PLANTER_BONUS_ITEM);
                        output.accept(ModItems.AUTO_TILLER_BONUS_ITEM);

                        output.accept(ModItems.MUMMY_WRAPPING_BONUS_ITEM);
                        output.accept(ModItems.TRIDENT_SHARD_BONUS_ITEM);
                        output.accept(ModItems.ZOMBIE_NAUTILUS_SHELL_BONUS_ITEM);
                        output.accept(ModItems.STRAY_CAPE_BONUS_ITEM);
                        output.accept(ModItems.VIAL_PUTRID_VENOM_BONUS_ITEM);
                    }))
                    .build()
    );
}