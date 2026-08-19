package net.necrocraft.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.alchemy.ModPotions;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModItemGroups {
    public static DeferredRegister<@NotNull CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NecroCraft.MODID);

    public static final Supplier<CreativeModeTab> NECROCRAFT= CREATIVE_MODE_TABS.register("necrocraft",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.NECROTIC_SHARD.get()))
                    .title(Component.translatable("itemGroup.necrocraft"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.NECROTIC_SHARD);
                        output.accept(ModItems.NECROTIC_POWDER);

                        output.accept(ModItems.SOUL_TOTEM);
                        output.accept(ModItems.REVOCATION_SCEPTER);

                        output.accept(ModItems.NECROTIC_APPLE);

                        output.accept(ModItems.SOUL_CARVING_TABLE);

                        output.accept(ModItems.POST_MORTEM_EXPLOSION_BONUS_ITEM);

                        output.accept(ModItems.HUNTER_BONUS_ITEM);
                        output.accept(ModItems.FARMER_BONUS_ITEM);

                        output.accept(ModItems.SOLID_SKIN_BONUS_ITEM);
                        output.accept(ModItems.HEALTH_BONUS_ITEM);
                        output.accept(ModItems.STORAGE_BONUS_ITEM);
                        output.accept(ModItems.AUTO_TILLER_BONUS_ITEM);
                        output.accept(ModItems.AUTO_PLANTER_BONUS_ITEM);
                        output.accept(ModItems.TRIAL_VAULT_BONUS_ITEM);

                        output.accept(ModItems.MUMMY_WRAPPING_BONUS_ITEM);

                        output.accept(ModItems.TRIDENT_SHARD_BONUS_ITEM);
                        output.accept(ModItems.ZOMBIE_NAUTILUS_SHELL_BONUS_ITEM);

                        output.accept(ModItems.STRAY_CAPE_BONUS_ITEM);
                    }))
                    .build()

    );
}
