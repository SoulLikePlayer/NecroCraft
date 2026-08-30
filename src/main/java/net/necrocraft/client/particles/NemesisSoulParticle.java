package net.necrocraft.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public class NemesisSoulParticle  extends RisingParticle {
    private final SpriteSet sprites;
    protected boolean isGlowing;

    public NemesisSoulParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd, sprites.first());
        this.sprites = sprites;
        this.scale(1.5F);
        this.setSpriteFromAge(sprites);
    }

    public int getLightCoords(float a) {
        return this.isGlowing ? LightCoordsUtil.withBlock(super.getLightCoords(a), 15) : super.getLightCoords(a);
    }

    public SingleQuadParticle.Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class EmissiveProvider implements ParticleProvider<@NotNull SimpleParticleType> {
        private final SpriteSet sprite;

        public EmissiveProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            NemesisSoulParticle particle = new NemesisSoulParticle(level, x, y, z, xAux, yAux, zAux, this.sprite);
            particle.setAlpha(1.0F);
            particle.isGlowing = true;
            return particle;
        }
    }

    public static class Provider implements ParticleProvider<@NotNull SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(SimpleParticleType options, @NotNull ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            NemesisSoulParticle particle = new NemesisSoulParticle(level, x, y, z, xAux, yAux, zAux, this.sprite);
            particle.setAlpha(1.0F);
            return particle;
        }
    }
}