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

    public static final Supplier<CreativeModeTab> NECROCRAFT_INGREDIENT = CREATIVE_MODE_TABS.register("necrocraft_ingredient",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.NECROTIC_SHARD.get()))
                    .title(Component.translatable("itemGroup.necrocraft.necrocraft_ingredient"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.NECROTIC_SHARD);
                    }))
                    .build()

    );

    public static final Supplier<CreativeModeTab> NECROCRAFT_EQUIPMENT = CREATIVE_MODE_TABS.register("necrocraft_equipment",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SOUL_TOTEM.get()))
                    .title(Component.translatable("itemGroup.necrocraft.necrocraft_equipment"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModItems.SOUL_TOTEM);
                    }))
                    .build()

    );
}
