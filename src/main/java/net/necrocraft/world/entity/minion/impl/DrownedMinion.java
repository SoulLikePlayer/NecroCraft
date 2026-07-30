package net.necrocraft.world.entity.minion.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

public class DrownedMinion extends ZombieMinion{
    private boolean searchingForLand;

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public DrownedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new DrownedMinionMoveControl(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new DrownedMinionSwimUpGoal(this, 1.5F, this.level().getSeaLevel()));
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner == null) {
            return;
        }

        if (this.isInWater() && owner.isInWater() && this.distanceTo(owner) <= 20) {
            int nearbyMinions = this.level().getEntitiesOfClass(
                    DrownedMinion.class,
                    this.getBoundingBox().inflate(20.0D),
                    other -> other.getOwner() == owner
            ).size();

            int amplifier = Mth.clamp(nearbyMinions - 1, 0, 4);

            int duration = 100;

            owner.addEffect(new MobEffectInstance(ModMobEffects.CURSE_OF_THE_SEA, duration, amplifier, false, false, true));
            this.addEffect(new MobEffectInstance(ModMobEffects.CURSE_OF_THE_SEA, duration, amplifier, false, false, true));
        }
    }

    /**
     * Uses an amphibious navigator (like vanilla Drowned) instead of the default
     * ground navigator, so the minion can properly pathfind through 3D bodies of
     * water instead of just walking along the bottom.
     */
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    /**
     * While submerged and wanting to swim, moves like a real swimmer instead of
     * falling under gravity. Without this override the minion sinks/walks along
     * the bottom instead of swimming toward its owner or target.
     */
    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        if (this.isUnderWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travelInWater(input, baseGravity, isFalling, oldY);
        }
    }

    /** Keeps the swimming animation/pose in sync with {@link #wantsToSwim()}. */
    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide()) {
            this.setSwimming(this.isEffectiveAi() && this.isUnderWater() && this.wantsToSwim());
        }
    }

    /** @return {@code true} if this minion should be rendered/animated as swimming */
    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming() && !this.isPassenger();
    }

    /** @return the ambient sound played while the minion is idle */
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DROWNED_AMBIENT;
    }

    /**
     * @param source the damage source that hurt the minion
     * @return the sound played when the minion takes damage
     */
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.DROWNED_HURT;
    }

    /** @return the sound played when the minion dies */
    protected SoundEvent getDeathSound() {
        return SoundEvents.DROWNED_DEATH;
    }

    /** @return the sound played on each footstep */
    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
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

    public void setSearchingForLand(boolean searchingForLand) {
        this.searchingForLand = searchingForLand;
    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos pos = path.getTarget();
            double sqrDistToNextPos = this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
            return sqrDistToNextPos < (double) 4.0F;
        }

        return false;
    }

    public boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        }

        LivingEntity owner = this.getOwner();
        if (owner != null && owner.isInWater()) {
            return true;
        }

        LivingEntity target = this.getTarget();
        return target != null && target.isInWater();
    }

    public boolean isSearchingForLand() {
        return this.searchingForLand;
    }

    private static class DrownedMinionMoveControl<T extends DrownedMinion> extends MoveControl<@NotNull T> {
        public DrownedMinionMoveControl(T drownedMinion) {
            super(drownedMinion);
        }

        public void tick() {
            LivingEntity target = (this.mob).getTarget();
            if (this.mob.wantsToSwim() && this.mob.isInWater()) {
                if (target != null && target.getY() > this.mob.getY() || this.mob.isSearchingForLand()) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0F, 0.002, 0.0F));
                }

                if (this.operation != Operation.MOVE_TO || this.mob.getNavigation().isDone()) {
                    this.mob.setSpeed(0.0F);
                    return;
                }

                double xd = this.wantedX - this.mob.getX();
                double yd = this.wantedY - this.mob.getY();
                double zd = this.wantedZ - this.mob.getZ();
                double dd = Math.sqrt(xd * xd + yd * yd + zd * zd);
                yd /= dd;
                float yRotD = (float)(Mth.atan2(zd, xd) * (double)180.0F / (double)(float)Math.PI) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotD, 90.0F));
                this.mob.yBodyRot = this.mob.getYRot();
                float targetSpeed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float newSpeed = Mth.lerp(0.125F, this.mob.getSpeed(), targetSpeed);
                this.mob.setSpeed(newSpeed);
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double)newSpeed * xd * 0.005, (double)newSpeed * yd * 0.1, (double)newSpeed * zd * 0.005));
            } else {
                if (!this.mob.onGround()) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0F, -0.008, 0.0F));
                }

                super.tick();
            }

        }
    }

    private static class DrownedMinionSwimUpGoal extends Goal {
        private final DrownedMinion minion;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public DrownedMinionSwimUpGoal(DrownedMinion minion, double speedModifier, int seaLevel) {
            this.minion = minion;
            this.speedModifier = speedModifier;
            this.seaLevel = seaLevel;
        }

        public boolean canUse() {
            return !this.minion.level().isBrightOutside() && this.minion.isInWater() && this.minion.getY() < (double)(this.seaLevel - 2);
        }

        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        public void tick() {
            if (this.minion.getY() < (double)(this.seaLevel - 1) && (this.minion.getNavigation().isDone() || this.minion.closeToNextPos())) {
                Vec3 nextPos = DefaultRandomPos.getPosTowards(this.minion, 4, 8, new Vec3(this.minion.getX(), this.seaLevel - 1, this.minion.getZ()), (float)Math.PI / 2F);
                if (nextPos == null) {
                    this.stuck = true;
                    return;
                }

                this.minion.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
            }

        }

        public void start() {
            this.minion.setSearchingForLand(true);
            this.stuck = false;
        }

        public void stop() {
            this.minion.setSearchingForLand(false);
        }
    }
}