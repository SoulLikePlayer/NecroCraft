package net.necrocraft.core;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.FloatTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<@NotNull AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NecroCraft.MODID);

    private static final String SOUL_GAUGE_KEY = "soul_gauge";

    public static final Supplier<AttachmentType<@NotNull Float>> SOUL_GAUGE = ATTACHMENT_TYPES.register(
            "soul_gauge",
            () -> AttachmentType.builder(() -> 0f)
                    .sync(ByteBufCodecs.FLOAT)
                    .serialize(new IAttachmentSerializer<>() {

                        @Override
                        public Float read(@NotNull IAttachmentHolder holder, @NotNull ValueInput input) {
                            return input.getFloatOr(SOUL_GAUGE_KEY, 0f);
                        }

                        @Override
                        public boolean write(Float attachment, @NotNull ValueOutput output) {
                            output.putFloat(SOUL_GAUGE_KEY, attachment);
                            return true;
                        }
                    })
                    .build()
    );
}
