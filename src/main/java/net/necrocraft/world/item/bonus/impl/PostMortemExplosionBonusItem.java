package net.necrocraft.world.item.bonus.impl;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostMortemExplosionBonusItem extends AbstractBonusItem {

    private static final float EXPLOSION_POWER = 3.0F;
    private static final double DAMAGE_RADIUS = EXPLOSION_POWER * 2.0D;
    private static final float MAX_DAMAGE = 15.0F;
    private static final double MAX_KNOCKBACK = 1.4D;

    public PostMortemExplosionBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.POST_MORTEM;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_DEATH;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        if (!(minion.level() instanceof ServerLevel level)) {
            return;
        }

        double x = minion.getX();
        double y = minion.getY() + (minion.getBbHeight() * 0.5D);
        double z = minion.getZ();

        spawnExplosionVisuals(level, x, y, z);
        applyExplosionDamage(minion, level, x, y, z);
    }

    private void spawnExplosionVisuals(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.HOSTILE, 4.0F,
                (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
    }

    private void applyExplosionDamage(AbstractMinion minion, ServerLevel level, double x, double y, double z) {
        LivingEntity owner = minion.getOwner();
        Vec3 center = new Vec3(x, y, z);

        AABB area = new AABB(
                x - DAMAGE_RADIUS, y - DAMAGE_RADIUS, z - DAMAGE_RADIUS,
                x + DAMAGE_RADIUS, y + DAMAGE_RADIUS, z + DAMAGE_RADIUS
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != minion && entity != owner && entity.isAlive());

        DamageSource explosionDamage = level.damageSources().explosion(minion, owner);

        for (LivingEntity target : targets) {
            double distance = target.position().distanceTo(center);
            if (distance >= DAMAGE_RADIUS) {
                continue;
            }

            double proximity = 1.0D - (distance / DAMAGE_RADIUS);

            float damage = (float) (proximity * MAX_DAMAGE);
            if (damage > 0.0F) {
                target.hurtServer(level, explosionDamage, damage);
            }

            Vec3 delta = target.position().subtract(center);
            double horizontalDist = Math.max(delta.horizontalDistance(), 0.01D);
            double knockbackStrength = proximity * MAX_KNOCKBACK;

            Vec3 knockback = new Vec3(
                    delta.x / horizontalDist * knockbackStrength,
                    0.4D * proximity + 0.15D,
                    delta.z / horizontalDist * knockbackStrength
            );

            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            target.hurtMarked = true;
        }
    }
}