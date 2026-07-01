package net.necrocraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record SoulData(Identifier entityType) {

    public static final Codec<SoulData> CODEC =
            Identifier.CODEC.xmap(SoulData::new, SoulData::entityType);

    public static final StreamCodec<ByteBuf, SoulData> STREAM_CODEC =
            Identifier.STREAM_CODEC.map(SoulData::new, SoulData::entityType);
}
