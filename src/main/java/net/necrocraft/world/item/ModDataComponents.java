package net.necrocraft.world.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.component.SoulData;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, NecroCraft.MODID);

    public static final Supplier<DataComponentType<SoulData>> SOUL_DATA = DATA_COMPONENTS.register(
            "soul_data",
            () -> DataComponentType.<SoulData>builder()
                    .persistent(SoulData.CODEC)
                    .networkSynchronized(SoulData.STREAM_CODEC)
                    .build()
    );
}