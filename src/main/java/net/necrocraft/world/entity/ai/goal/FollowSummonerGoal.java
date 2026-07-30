package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.entity.minion.impl.DrownedMinion;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Makes a minion walk toward, and stay near, its owner, pathing to them once
 * they stray further than {@code startDistance} away and stopping once back
 * within {@code stopDistance}. Falls back to teleporting when the minion is
 * unable to path to a far-away owner (see {@link AbstractMinion#shouldTryTeleportToOwner()}).
 */
public class FollowSummonerGoal extends Goal {
    private final AbstractMinion minion;
    private @Nullable LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    /**
     * @param minion         the minion that should follow its summoner
     * @param speedModifier  movement speed multiplier applied while pathing to the owner
     * @param startDistance  distance beyond which the minion starts following its owner
     * @param stopDistance   distance within which the minion stops following its owner
     * @throws IllegalArgumentException if the minion's navigation is neither ground- nor flying-based
     */
    public FollowSummonerGoal(AbstractMinion minion, double speedModifier, float startDistance, float stopDistance) {
        this.minion = minion;
        this.speedModifier = speedModifier;
        this.navigation = minion.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(minion.getNavigation() instanceof GroundPathNavigation)
                && !(minion.getNavigation() instanceof FlyingPathNavigation)
                && !(minion.getNavigation() instanceof AmphibiousPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowSummonMinion");
        }
    }

    /**
     * Determines whether the minion should start following its owner: it must
     * have an owner, be able to move toward them, and currently be farther
     * away than {@code startDistance}.
     *
     * @return {@code true} if the goal should start
     */
    public boolean canUse() {
        LivingEntity owner = this.minion.getOwner();
        if (owner == null) {
            return false;
        } else if (this.minion.unableToMoveToOwner()) {
            return false;
        } else if (this.minion.distanceToSqr(owner) < (double)(this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = owner;
            return true;
        }
    }

    /**
     * Determines whether the goal should keep running: it stops once the
     * navigation is finished, the minion can no longer reach its owner, or
     * the minion has come back within {@code stopDistance}.
     *
     * @return {@code true} if the goal should continue
     */
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else {
            return this.minion.unableToMoveToOwner() ? false : !(this.minion.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
        }
    }

    /**
     * Resets the path recalculation timer and temporarily removes the water
     * pathfinding penalty so the minion can cross water to reach its owner.
     */
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.minion.getPathfindingMalus(PathType.WATER);
        this.minion.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    /**
     * Clears the tracked owner, stops navigation and restores the original
     * water pathfinding penalty.
     */
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.minion.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    /**
     * Each tick, looks at the owner (unless they are far enough away that a
     * teleport is imminent) and, on the path-recalculation interval, either
     * attempts to teleport to the owner or recomputes the path toward them.
     */
    public void tick() {
        boolean isOwnerFarAway = this.minion.shouldTryTeleportToOwner();
        if (!isOwnerFarAway) {
            this.minion.getLookControl().setLookAt(this.owner, 10.0F, (float)this.minion.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (isOwnerFarAway) {
                this.minion.tryToTeleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.speedModifier);
            }
        }

    }
}