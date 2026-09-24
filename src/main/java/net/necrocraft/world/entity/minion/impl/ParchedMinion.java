package net.necrocraft.world.entity.minion.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ParchedMinion extends SkeletonMinion {

    static{
        SYNCED_BONUS.add(ModItems.MUMMY_WRAPPING_BONUS_ITEM);
    }

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public ParchedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable Holder<@NotNull MobEffect> getMinionEffect() {
        return  MobEffects.WEAKNESS;
    }

    /** @return the ambient sound played while the minion is idle */
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARCHED_AMBIENT;
    }

    /**
     * @param source the damage source that hurt the minion
     * @return the sound played when the minion takes damage
     */
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.PARCHED_HURT;
    }

    /** @return the sound played when the minion dies */
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARCHED_DEATH;
    }

    /** @return the sound played on each footstep */
    protected SoundEvent getStepSound() {
        return SoundEvents.PARCHED_STEP;
    }

    /**
     * Plays the minion's footstep sound at reduced volume.
     *
     * @param pos        the block position of the step
     * @param blockState the state of the block being stepped on
     */
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }
}