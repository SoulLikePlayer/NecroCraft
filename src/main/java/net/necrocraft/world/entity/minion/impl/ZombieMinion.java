package net.necrocraft.world.entity.minion.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.entity.minion.registry.MinionEvolution;
import net.necrocraft.world.entity.minion.registry.MinionEvolutions;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * A tamed minion variant of the vanilla {@code Zombie}, reusing the
 * zombie's ambient, hurt, death and step sounds.
 */
public class ZombieMinion extends AbstractMinion {

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public ZombieMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    /** @return the ambient sound played while the minion is idle */
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    /**
     * @param source the damage source that hurt the minion
     * @return the sound played when the minion takes damage
     */
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    /** @return the sound played when the minion dies */
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    /** @return the sound played on each footstep */
    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
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