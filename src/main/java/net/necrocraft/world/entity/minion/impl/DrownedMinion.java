package net.necrocraft.world.entity.minion.impl;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.effect.ModMobEffects;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrownedMinion extends ZombieMinion {
    private boolean searchingForLand;

    private static final EntityDataAccessor<@NotNull Byte> DATA_DASH_PHASE =
            SynchedEntityData.defineId(DrownedMinion.class, EntityDataSerializers.BYTE);

    public static final byte DASH_PHASE_IDLE = 0;
    public static final byte DASH_PHASE_PRE_DASH = 1;
    public static final byte DASH_PHASE_DASHING = 2;
    public static final byte DASH_PHASE_POST_DASH = 3;


    static {
        SYNCED_BONUS.add(ModItems.TRIDENT_SHARD_BONUS_ITEM);
    }

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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DASH_PHASE, DASH_PHASE_IDLE);
    }


    void setDashPhase(byte phase) {
        this.entityData.set(DATA_DASH_PHASE, phase);
    }

    public byte getDashPhase() {
        return this.entityData.get(DATA_DASH_PHASE);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TridentShardDashAttackGoal(this));
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
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    /**
     * While submerged and wanting to swim, moves like a real swimmer instead of
     * falling under gravity. Without this override the minion sinks/walks along
     * the bottom instead of swimming toward its owner or target.
     */
    @Override
    protected void travelInWater(@NotNull Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        if (this.isUnderWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travelInWater(input, baseGravity, isFalling, oldY);
        }
    }

    /**
     * Keeps the swimming animation/pose in sync with {@link #wantsToSwim()}.
     */
    @Override
    public void updateSwimming() {
        if (!this.level().isClientSide()) {
            this.setSwimming(this.isEffectiveAi() && this.isUnderWater() && this.wantsToSwim());
        }
    }

    /**
     * @return {@code true} if this minion should be rendered/animated as swimming
     */
    @Override
    public boolean isVisuallySwimming() {
        return this.isSwimming() && !this.isPassenger();
    }

    /**
     * @return the ambient sound played while the minion is idle
     */
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

    /**
     * @return the sound played when the minion dies
     */
    protected SoundEvent getDeathSound() {
        return SoundEvents.DROWNED_DEATH;
    }

    /**
     * @return the sound played on each footstep
     */
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

    /**
     * @return {@code true} if this minion has the Trident Shard bonus equipped, unlocking its dash attack
     */
    public boolean hasTridentShardBonus() {
        return this.getBonuses().contains(ModItems.TRIDENT_SHARD_BONUS_ITEM.getId());
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
                float yRotD = (float) (Mth.atan2(zd, xd) * (double) 180.0F / (double) (float) Math.PI) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotD, 90.0F));
                this.mob.yBodyRot = this.mob.getYRot();
                float targetSpeed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float newSpeed = Mth.lerp(0.125F, this.mob.getSpeed(), targetSpeed);
                this.mob.setSpeed(newSpeed);
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double) newSpeed * xd * 0.005, (double) newSpeed * yd * 0.1, (double) newSpeed * zd * 0.005));
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
            return !this.minion.level().isBrightOutside()
                    && this.minion.isInWater()
                    && this.minion.getY() < (double) (this.seaLevel - 2);
        }

        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        public void tick() {
            if (this.minion.getY() < (double) (this.seaLevel - 1) && (this.minion.getNavigation().isDone() || this.minion.closeToNextPos())) {
                Vec3 nextPos = DefaultRandomPos.getPosTowards(this.minion, 4, 8, new Vec3(this.minion.getX(), this.seaLevel - 1, this.minion.getZ()), (float) Math.PI / 2F);
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

    /**
     * While the minion has the Trident Shard bonus equipped, is submerged,
     * and has a target that is too far away to melee, this lets it dash
     * through the water toward that target. Any living entity caught along
     * the dash path (the owner excluded) is hurt and knocked back, and the
     * target itself takes bonus damage if the dash connects.
     */
    private static class TridentShardDashAttackGoal extends Goal {
        private static final double MAX_DISTANCE = 16.0D;
        private static final double MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;

        private static final int PRE_DASH_TICKS = 12;
        private static final int DASH_DURATION_TICKS = 14;
        private static final int POST_DASH_TICKS = 10;
        private static final int POST_DASH_HIT_DURATION = 6;
        private static final int COOLDOWN_TICKS = 24;

        private static final double MIN_STEP = 0.35D;
        private static final double MAX_STEP = 1.15D;

        private static final float TARGET_DAMAGE = 8.0F;
        private static final float SWEEP_DAMAGE = 4.0F;
        private static final float KNOCKBACK_STRENGTH = 0.8F;
        private static final double HIT_RADIUS = 1.2D;
        private static final int BUBBLES_PER_TICK = 6;

        private final DrownedMinion minion;
        private final Set<LivingEntity> alreadyHit = new HashSet<>();

        /**
         * Direction locked at the moment the windup begins – never mutated during the dash.
         */
        private Vec3 dashDirection = Vec3.ZERO;

        /**
         * Body yaw matching {@link #dashDirection}, computed once in {@link #start()}
         * and re-applied every tick of the windup and dash so nothing else (pathing,
         * head-only look control, etc.) can turn the body away from it mid-attack.
         */
        private float dashYaw;

        private int preDashTicksLeft;
        private int dashTicksLeft;
        private int postDashTicksLeft;
        private int hitWindowLeft;
        private int cooldown;
        private boolean cooldownReadyLogged = true;

        TridentShardDashAttackGoal(DrownedMinion minion) {
            this.minion = minion;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }


        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                if (this.cooldown <= 0) {
                    this.cooldownReadyLogged = true;
                }
                return false;
            }
            if (!this.minion.hasTridentShardBonus() || !this.minion.isInWater()) return false;

            LivingEntity target = this.minion.getTarget();
            if (target == null || !target.isAlive()) return false;

            double distSq = this.minion.distanceToSqr(target);
            boolean canDash = distSq <= MAX_DISTANCE_SQ;
            if (canDash && this.cooldownReadyLogged) {
                this.cooldownReadyLogged = false;
            }
            return canDash;
        }

        @Override
        public boolean canContinueToUse() {
            return this.preDashTicksLeft > 0
                    || this.dashTicksLeft > 0
                    || this.postDashTicksLeft > 0
                    || this.hitWindowLeft > 0;
        }

        @Override
        public void start() {
            LivingEntity target = this.minion.getTarget();


            if (target != null) {
                Vec3 diff = target.position().subtract(this.minion.position());
                this.dashDirection = diff.lengthSqr() > 1.0E-4 ? diff.normalize() : Vec3.ZERO;
            } else {
                this.dashDirection = this.minion.getLookAngle();
            }

            this.dashYaw = (float) (Mth.atan2(this.dashDirection.z, this.dashDirection.x) * (180.0 / Math.PI)) - 90.0F;

            this.preDashTicksLeft = PRE_DASH_TICKS;
            this.dashTicksLeft = 0;
            this.postDashTicksLeft = 0;
            this.hitWindowLeft = 0;
            this.alreadyHit.clear();

            this.minion.setDashPhase(DASH_PHASE_PRE_DASH);
            this.minion.setYRot(this.dashYaw);
            this.minion.yRotO = this.dashYaw;
            this.minion.yBodyRot = this.dashYaw;
            this.minion.yBodyRotO = this.dashYaw;
            this.minion.setYHeadRot(this.dashYaw);
            this.minion.yHeadRotO = this.dashYaw;

            Vec3 dir = this.dashDirection;
            this.minion.getLookControl().setLookAt(
                    this.minion.getX() + dir.x,
                    this.minion.getY() + dir.y,
                    this.minion.getZ() + dir.z,
                    30.0F, 30.0F);
        }

        @Override
        public void stop() {
            this.preDashTicksLeft = 0;
            this.dashTicksLeft = 0;
            this.postDashTicksLeft = 0;
            this.hitWindowLeft = 0;
            this.cooldown = COOLDOWN_TICKS;

            this.minion.setDashPhase(DASH_PHASE_IDLE);
            this.minion.setDeltaMovement(this.minion.getDeltaMovement().scale(0.2D));
        }


        @Override
        public void tick() {
            if (this.preDashTicksLeft > 0) {
                tickPreDash();
            } else if (this.dashTicksLeft > 0) {
                tickDash();
            } else if (this.postDashTicksLeft > 0 || this.hitWindowLeft > 0) {
                tickPostDash();
            }
        }


        /**
         * Windup: minion freezes and leans forward.
         */
        private void tickPreDash() {

            this.minion.setDeltaMovement(Vec3.ZERO);

            this.minion.setYRot(this.dashYaw);
            this.minion.yBodyRot = this.dashYaw;

            Vec3 dir = this.dashDirection;
            this.minion.getLookControl().setLookAt(
                    this.minion.getX() + dir.x * 5,
                    this.minion.getY() + dir.y * 5,
                    this.minion.getZ() + dir.z * 5,
                    30.0F, 30.0F);

            this.preDashTicksLeft--;

            if (this.preDashTicksLeft <= 0) {
                this.dashTicksLeft = DASH_DURATION_TICKS;
                this.minion.setDashPhase(DASH_PHASE_DASHING);
                this.minion.playSound(SoundEvents.TRIDENT_RIPTIDE_1.value(), 1.0F, 0.9F);
            }
        }

        /**
         * Dash: straight-line movement along the frozen {@link #dashDirection}.
         * The direction is NEVER recalculated here.
         */
        private void tickDash() {
            this.minion.setYRot(this.dashYaw);
            this.minion.yBodyRot = this.dashYaw;
            this.minion.setYHeadRot(this.dashYaw);

            double step = Mth.clamp(

                    MAX_STEP * ((double) this.dashTicksLeft / DASH_DURATION_TICKS)
                            + MIN_STEP * (1.0 - (double) this.dashTicksLeft / DASH_DURATION_TICKS),
                    MIN_STEP, MAX_STEP);

            this.minion.setDeltaMovement(this.dashDirection.scale(step));

            this.spawnBubbleTrail();
            this.applyTouchDamage(true);

            this.dashTicksLeft--;
            if (this.dashTicksLeft <= 0) {

                this.postDashTicksLeft = POST_DASH_TICKS;
                this.hitWindowLeft = POST_DASH_HIT_DURATION;
                this.minion.setDashPhase(DASH_PHASE_POST_DASH);
            }
        }

        /**
         * Recovery: momentum bleeds off, hit window still active.
         */
        private void tickPostDash() {
            this.minion.setDeltaMovement(this.minion.getDeltaMovement().scale(0.85D));

            this.spawnBubbleTrail();
            if (this.hitWindowLeft > 0) {
                this.applyTouchDamage(false);
                this.hitWindowLeft--;
            }

            if (this.postDashTicksLeft > 0) {
                this.postDashTicksLeft--;
            }
        }

        private void spawnBubbleTrail() {
            if (this.minion.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE,
                        this.minion.getX(),
                        this.minion.getY() + this.minion.getBbHeight() * 0.5D,
                        this.minion.getZ(),
                        BUBBLES_PER_TICK,
                        this.minion.getBbWidth() * 0.3D,
                        this.minion.getBbHeight() * 0.3D,
                        this.minion.getBbWidth() * 0.3D,
                        0.01D);
            }
        }

        private void applyTouchDamage(boolean inMovementPhase) {
            if (!(this.minion.level() instanceof ServerLevel serverLevel)) return;

            LivingEntity target = this.minion.getTarget();
            List<LivingEntity> hits = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    this.minion.getBoundingBox().inflate(HIT_RADIUS),
                    e -> e != this.minion
                            && e != this.minion.getOwner()
                            && e.isAlive()
                            && !this.alreadyHit.contains(e));

            for (LivingEntity hit : hits) {
                this.alreadyHit.add(hit);
                boolean isTarget = hit == target;
                float damage = isTarget ? TARGET_DAMAGE : SWEEP_DAMAGE;

                hit.hurtServer(serverLevel, this.minion.damageSources().mobAttack(this.minion), damage);

                Vec3 away = hit.position().subtract(this.minion.position());
                away = away.lengthSqr() > 1.0E-4 ? away.normalize() : this.dashDirection;
                hit.knockback(-away.x, -away.y, -away.z,
                        this.minion.damageSources().mobAttack(this.minion), KNOCKBACK_STRENGTH);
                hit.setDeltaMovement(hit.getDeltaMovement().add(0.0D, 0.15D, 0.0D));

                if (isTarget && inMovementPhase && this.dashTicksLeft > 1) {
                    this.dashTicksLeft = 1;
                }
            }
        }
    }
}