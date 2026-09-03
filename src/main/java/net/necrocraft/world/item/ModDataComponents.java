package net.necrocraft.world.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.component.ObedienceActionData;
import net.necrocraft.world.item.component.SoulData;
import net.necrocraft.world.item.equipment.ObedienceScepter;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister<@NotNull DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, NecroCraft.MODID);

    public static final Supplier<DataComponentType<@NotNull SoulData>> SOUL_DATA = DATA_COMPONENTS.register(
            "soul_data",
            () -> DataComponentType.<SoulData>builder()
                    .persistent(SoulData.CODEC)
                    .networkSynchronized(SoulData.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<DataComponentType<@NotNull ObedienceActionData>> OBEDIENCE_ACTION_DATA = DATA_COMPONENTS.register(
            "obedience_action_data",
            () -> DataComponentType.<ObedienceActionData>builder()
                    .persistent(ObedienceActionData.CODEC)
                    .networkSynchronized(ObedienceActionData.STREAM_CODEC)
                    .build()
    );
}