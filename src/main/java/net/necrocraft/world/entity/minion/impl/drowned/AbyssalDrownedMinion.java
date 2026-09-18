package net.necrocraft.world.entity.minion.impl.drowned;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.necrocraft.world.entity.ai.goal.GatedGoal;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * A drowned minion that, instead of the Trident Shard dash, drags its target
 * into the depths.
 * <p>
 * When its target is close enough, can actually die from drowning, and there is
 * enough water below, the minion grabs it, dives, and holds it under until it
 * drowns. When any of these conditions is not met (target can breathe
 * underwater, is invulnerable, water too shallow...) it simply falls back to the
 * regular melee attack inherited from {@link net.necrocraft.world.entity.minion.AbstractMinion}.
 * <p>
 * Grabbing does not make the minion invulnerable nor immobilize it: it can be
 * attacked (and killed) normally while holding, which releases the target.
 */
public class AbyssalDrownedMinion extends DrownedMinion {

    /**
     * Synced so the client can play a "holding" animation.
     */
    private static final EntityDataAccessor<@NotNull Boolean> DATA_GRABBING =
            SynchedEntityData.defineId(AbyssalDrownedMinion.class, EntityDataSerializers.BOOLEAN);

    static {
        SYNCED_BONUS.remove(ModItems.TRIDENT_SHARD_BONUS_ITEM);
    }

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public AbyssalDrownedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_GRABBING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(-1, new DragToTheDepthsGoal(this));
        this.goalSelector.addGoal(1, new GatedGoal(
                new MeleeAttackGoal(this, 1.0D, true),
                () -> !this.isSedentary() && !this.isGrabbing()
        ));
    }

    @Override
    protected void applyTickEffect() {}

    /**
     * @return {@code true} while this minion is holding a target
     */
    public boolean isGrabbing() {
        return this.entityData.get(DATA_GRABBING);
    }

    private void setGrabbing(boolean grabbing) {
        this.entityData.set(DATA_GRABBING, grabbing);
    }

    /**
     * A minion that is dragging someone down always wants to swim, even if
     * neither its owner nor its target is in the water anymore.
     */
    @Override
    public boolean wantsToSwim() {
        return this.isGrabbing() || super.wantsToSwim();
    }

    /**
     * Never teleports away from its prey.
     */
    @Override
    public void tryToTeleportToOwner() {
        if (!this.isGrabbing()) {
            super.tryToTeleportToOwner();
        }
    }

    /**
     * Grabs the current target and drags it down until it drowns, the target
     * escapes, becomes immune, or the minion dies.
     */
    private static class DragToTheDepthsGoal extends Goal {
        private static final int MIN_WATER_DEPTH = 2;

        private static final int DRAG_DEPTH = 8;
        private static final double DIVE_SPEED = 0.12D;

        private static final int MAX_GRAB_TICKS = 300;
        private static final int COOLDOWN_TICKS = 160;

        private static final double RELEASE_DISTANCE_SQ = 6.0D * 6.0D;

        private static final double HOLD_DISTANCE = 0.9D;
        private static final double PULL_FACTOR = 0.7D;
        private static final double MAX_PULL_SPEED = 0.5D;

        private static final int AIR_DRAIN_PER_TICK = 6;
        private static final int DROWN_INTERVAL_TICKS = 20;
        private static final float DROWN_DAMAGE = 4.0F;

        private static final double MAX_TARGET_WIDTH = 2.0D;
        private static final double MAX_TARGET_HEIGHT = 3.0D;

        private final AbyssalDrownedMinion minion;
        private @Nullable LivingEntity grabbed;

        private int grabTicks;
        private int drownCooldown;
        private int nextGrabTick;
        private double startY;

        DragToTheDepthsGoal(AbyssalDrownedMinion minion) {
            this.minion = minion;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        /**
         * Needed: by default running goals are only ticked every other tick,
         * which would make the pull on the target choppy.
         */
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (this.minion.tickCount < this.nextGrabTick || this.minion.isSedentary()) return false;

            LivingEntity target = this.minion.getTarget();
            if (target == null || !target.isAlive() || !this.minion.isInWater()) return false;
            if (!(this.minion.level() instanceof ServerLevel level)) return false;

            double distSq = this.minion.distanceToSqr(target);
            if (distSq > 6.0D * 6.0D) return false;

            boolean drowned = canBeDrowned(level, this.minion, target);
            int depth = this.waterDepthBelow(level);

            System.out.println("[Abyssal] canBeDrowned=" + drowned + " depth=" + depth + " target=" + target.getName().getString());

            return drowned && depth >= MIN_WATER_DEPTH;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.grabbed;
            if (target == null || !target.isAlive() || !this.minion.isAlive()) {
                return false;
            }
            if (!(this.minion.level() instanceof ServerLevel level)) {
                return false;
            }
            return this.grabTicks < MAX_GRAB_TICKS
                    && this.minion.isInWater()
                    && this.minion.distanceToSqr(target) <= RELEASE_DISTANCE_SQ
                    && canBeDrowned(level, this.minion, target);
        }

        @Override
        public void start() {
            this.grabbed = this.minion.getTarget();
            this.grabTicks = 0;
            this.drownCooldown = 0;
            this.startY = this.minion.getY();

            this.minion.setGrabbing(true);
            this.minion.getNavigation().stop();
            this.minion.playSound(SoundEvents.DROWNED_AMBIENT_WATER, 1.0F, 0.6F);
        }

        @Override
        public void stop() {
            this.grabbed = null;
            this.minion.setGrabbing(false);
            this.minion.setDeltaMovement(this.minion.getDeltaMovement().scale(0.2D));
            this.nextGrabTick = this.minion.tickCount + COOLDOWN_TICKS;
        }

        @Override
        public void tick() {
            LivingEntity target = this.grabbed;
            if (target == null || !(this.minion.level() instanceof ServerLevel level)) {
                return;
            }
            this.grabTicks++;

            this.minion.getNavigation().stop();
            this.minion.getLookControl().setLookAt(target, 30.0F, 30.0F);

            this.dive(level);
            this.holdTarget(target);
            this.drown(level, target);
            this.spawnBubbles(level, target);
        }

        /**
         * The minion sinks straight down until it reached {@link #DRAG_DEPTH}
         * or the seabed, then just hovers in place.
         */
        private void dive(ServerLevel level) {
            boolean canDive = this.minion.getY() > this.startY - DRAG_DEPTH && this.waterDepthBelow(level) >= 1;
            this.minion.setDeltaMovement(0.0D, canDive ? -DIVE_SPEED : 0.0D, 0.0D);
        }

        /**
         * Keeps the target glued next to the minion by overriding its velocity
         * every tick. The target can still be hit and can still hit back.
         */
        private void holdTarget(LivingEntity target) {
            Vec3 dir = new Vec3(target.getX() - this.minion.getX(), 0.0D, target.getZ() - this.minion.getZ());
            if (dir.lengthSqr() < 1.0E-4D) {
                Vec3 look = this.minion.getLookAngle();
                dir = new Vec3(look.x, 0.0D, look.z);
            }
            dir = dir.lengthSqr() > 1.0E-4D ? dir.normalize() : Vec3.ZERO;

            Vec3 holdPos = this.minion.position()
                    .add(dir.scale(HOLD_DISTANCE))
                    .add(0.0D, -0.3D, 0.0D);

            Vec3 pull = holdPos.subtract(target.position()).scale(PULL_FACTOR);
            double length = pull.length();
            if (length > MAX_PULL_SPEED) {
                pull = pull.scale(MAX_PULL_SPEED / length);
            }

            target.setDeltaMovement(pull.add(this.minion.getDeltaMovement()));
            target.hurtMarked = true;
            if (target instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        }

        /**
         * While the target's head is underwater: burns through its air supply
         * quickly, then deals drowning damage attributed to the minion (so kills
         * count for it: {@code killedEntity}, loot, death message...).
         * <p>
         * The air supply is pinned at 0 once empty so vanilla never reaches -20
         * and never applies its own (unattributed) drowning damage on top of ours.
         */
        private void drown(ServerLevel level, LivingEntity target) {
            if (this.drownCooldown > 0) {
                this.drownCooldown--;
            }
            if (!target.isEyeInFluid(FluidTags.WATER)) {
                return;
            }

            int air = target.getAirSupply();
            if (air > 0) {
                target.setAirSupply(Math.max(0, air - AIR_DRAIN_PER_TICK));
                return;
            }

            target.setAirSupply(0);
            if (this.drownCooldown <= 0) {
                this.drownCooldown = DROWN_INTERVAL_TICKS;
                DamageSource source = this.minion.damageSources().source(DamageTypes.DROWN, null, this.minion);
                target.hurtServer(level, source, DROWN_DAMAGE);
            }
        }

        private void spawnBubbles(ServerLevel level, LivingEntity target) {
            level.sendParticles(ParticleTypes.BUBBLE,
                    target.getX(), target.getEyeY(), target.getZ(),
                    4, 0.25D, 0.25D, 0.25D, 0.02D);
        }

        /**
         * @return the number of consecutive water blocks directly below the minion, capped at {@link #DRAG_DEPTH}
         */
        private int waterDepthBelow(ServerLevel level) {
            BlockPos.MutableBlockPos pos = this.minion.blockPosition().mutable();
            int depth = 0;
            while (depth < DRAG_DEPTH) {
                pos.move(Direction.DOWN);
                if (!level.getFluidState(pos).is(FluidTags.WATER)) {
                    break;
                }
                depth++;
            }
            return depth;
        }

        /**
         * A target can be dragged down only if it can really die from drowning:
         * no water breathing (natural, effect or conduit), no invulnerability
         * (creative/spectator, drowning gamerule...), not the owner, not riding
         * something, and small enough to be held.
         */
        private static boolean canBeDrowned(ServerLevel level, AbyssalDrownedMinion minion, LivingEntity target) {
            if ((target == minion.getOwner() || target.isPassenger()) && !minion.getNemesis()){
                return false;
            }
            if (target.isInvulnerable() || target.canBreatheUnderwater() || MobEffectUtil.hasWaterBreathing(target)) {
                return false;
            }
            if (target instanceof Player player && (player.getAbilities().invulnerable || player.isSpectator())) {
                return false;
            }
            if (target.getBbWidth() > MAX_TARGET_WIDTH || target.getBbHeight() > MAX_TARGET_HEIGHT) {
                return false;
            }
            return !target.isInvulnerableTo(level, minion.damageSources().source(DamageTypes.DROWN, null, minion));
        }
    }
}