package net.necrocraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SoulData(Identifier entityType, List<ItemStack> equipment, List<Identifier> bonuses) {

    public static final int EQUIPMENT_SIZE = 6;
    public static final int MAX_BONUSES = 8;

    public static final Codec<SoulData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("entity_type").forGetter(SoulData::entityType),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("equipment").forGetter(SoulData::equipment),
            Identifier.CODEC.listOf(0, MAX_BONUSES).optionalFieldOf("bonuses", List.of()).forGetter(SoulData::bonuses)
    ).apply(instance, SoulData::new));

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull SoulData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, SoulData::entityType,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SoulData::equipment,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), SoulData::bonuses,
            SoulData::new
    );

    public SoulData {
        if (bonuses.size() > MAX_BONUSES) {
            throw new IllegalArgumentException(
                    "Une pierre d'âme ne peut porter que " + MAX_BONUSES + " bonus au maximum (reçu " + bonuses.size() + ")");
        }
    }

    public SoulData(Identifier entityType) {
        this(entityType, emptyEquipment(), List.of());
    }

    public static List<ItemStack> emptyEquipment() {
        return new ArrayList<>(List.of(
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        ));
    }

    public SoulData withEquipment(List<ItemStack> newEquipment) {
        return new SoulData(this.entityType, newEquipment, this.bonuses);
    }

    public SoulData withBonuses(List<Identifier> newBonuses) {
        return new SoulData(this.entityType, this.equipment, List.copyOf(newBonuses));
    }
}