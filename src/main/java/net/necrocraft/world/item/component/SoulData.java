package net.necrocraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record SoulData(Identifier entityType, List<ItemStack> equipment) {

    public static final int EQUIPMENT_SIZE = 6;

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("entity_type").forGetter(SoulData::entityType),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("equipment").forGetter(SoulData::equipment)
    ).apply(instance, SoulData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, SoulData::entityType,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SoulData::equipment,
            SoulData::new
    );

    public SoulData(Identifier entityType) {
        this(entityType, emptyEquipment());
    }

    public static List<ItemStack> emptyEquipment() {
        return new ArrayList<>(List.of(
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));
    }

    public SoulData withEquipment(List<ItemStack> newEquipment) {
        return new SoulData(this.entityType, newEquipment);
    }
}