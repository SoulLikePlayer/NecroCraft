package net.necrocraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.necrocraft.world.item.equipment.ObedienceScepter;
import org.jetbrains.annotations.NotNull;

public record ObedienceActionData(ObedienceScepter.ObedienceAction action) {
    public static final Codec<ObedienceActionData> CODEC =
            ObedienceScepter.ObedienceAction.CODEC.xmap(ObedienceActionData::new, ObedienceActionData::action);

    public static final StreamCodec<@NotNull ByteBuf, @NotNull ObedienceActionData> STREAM_CODEC =
            ObedienceScepter.ObedienceAction.STREAM_CODEC.map(ObedienceActionData::new, ObedienceActionData::action);

    public ObedienceActionData next() {
        return new ObedienceActionData(action.next());
    }
}