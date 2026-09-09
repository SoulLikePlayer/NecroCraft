package net.necrocraft.world.entity.monster.skeleton;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class Echoing extends AbstractSkeleton {
    public Echoing(EntityType<? extends @NotNull AbstractSkeleton> type, Level level) {
        super(type, level);
    }

    protected @NotNull AbstractArrow getArrow(@NotNull ItemStack projectile, float power, @Nullable ItemStack firingWeapon) {
        AbstractArrow arrow = super.getArrow(projectile, power, firingWeapon);
        if (arrow instanceof Arrow arrow2) {
            arrow2.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600));
        }

        return arrow;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected @NotNull SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    protected int getHardAttackInterval() {
        return 50;
    }

    protected int getAttackInterval() {
        return 70;
    }
}
