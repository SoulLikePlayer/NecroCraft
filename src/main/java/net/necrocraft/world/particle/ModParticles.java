package net.necrocraft.world.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.necrocraft.core.NecroCraft;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModParticles {
    public static final DeferredRegister<@NotNull ParticleType<?>> PARTICLE_TYPE =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, NecroCraft.MODID);

    public static final DeferredHolder<@NotNull ParticleType<?>, @NotNull SimpleParticleType>  NEMESIS_SOUL =
            PARTICLE_TYPE.register("nemesis_soul", () -> new SimpleParticleType(false));
}
