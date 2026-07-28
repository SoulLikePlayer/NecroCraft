package net.necrocraft.world.entity.ai.goal.hunter;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Makes a "hunter" minion periodically scan a surrounding area for the
 * closest living entity of one of its huntable types (see
 * {@link AbstractMinion#getHuntableTargets()}) and target it, provided the
 * minion has no current target.
 */
public class HuntPreyGoal extends TargetGoal {
    private final AbstractMinion minion;
    private final float range;
    private LivingEntity prey;
    private int cooldown;

    /**
     * @param minion the minion that should hunt nearby prey
     * @param range  the radius (in blocks) to search around the minion for prey
     */
    public HuntPreyGoal(AbstractMinion minion, float range) {
        super(minion, false);
        this.minion = minion;
        this.range = range;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Determines whether the goal should start: the minion must not already
     * have a target, the hunt cooldown must have elapsed, the minion must
     * have at least one huntable entity type configured, and a living
     * instance of one of those types must be found within {@link #range}.
     * The closest such entity is selected as {@link #prey}.
     *
     * @return {@code true} if prey was found and the goal should start
     */
    @Override
    public boolean canUse() {
        if (this.minion.getTarget() != null) {
            return false;
        }
        if (--this.cooldown > 0) {
            return false;
        }
        this.cooldown = reducedTickDelay(60);

        Set<EntityType<?>> huntable = this.minion.getHuntableTargets();
        if (huntable.isEmpty()) {
            return false;
        }

        AABB area = this.minion.getBoundingBox().inflate(this.range);
        List<LivingEntity> preys = this.minion.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive() && huntable.contains(entity.getType()));

        this.prey = preys.stream()
                .min(Comparator.comparingDouble(this.minion::distanceToSqr))
                .orElse(null);

        return this.prey != null;
    }

    /** Sets the minion's target to the selected prey. */
    @Override
    public void start() {
        this.mob.setTarget(this.prey);
        super.start();
    }
}