package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.EnumSet;

/**
 * Makes a minion assist its owner offensively: whenever the owner's
 * "last hurt" entity changes (i.e. the owner attacks something new), the
 * minion targets that same entity.
 */
public class SummonerHurtTargetGoal extends TargetGoal {
    private final AbstractMinion minion;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    /** @param minion the minion whose target should follow its owner's attacks */
    public SummonerHurtTargetGoal(AbstractMinion minion) {
        super(minion, false);
        this.minion = minion;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Checks whether the owner has a new "last hurt" entity (by comparing
     * timestamps) that is a valid attack target.
     *
     * @return {@code true} if the goal should start targeting the owner's latest victim
     */
    public boolean canUse() {
        LivingEntity owner = this.minion.getOwner();
        if (owner == null) {
            return false;
        } else {
            this.ownerLastHurt = owner.getLastHurtMob();
            int ts = owner.getLastHurtMobTimestamp();
            return ts != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
        }
    }

    /**
     * Sets the minion's target to the owner's last victim and records the
     * current timestamp so the same event isn't reused.
     */
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.minion.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }
}