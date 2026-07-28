package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.EnumSet;

/**
 * Makes a minion retaliate against whoever last attacked its owner: whenever
 * the owner's "last hurt by" entity changes, the minion targets that entity too.
 */
public class SummonerHurtByTargetGoal extends TargetGoal {
    private final AbstractMinion minion;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    /** @param minion the minion whose target should follow its owner's attacker */
    public SummonerHurtByTargetGoal(AbstractMinion minion) {
        super(minion, false);
        this.minion = minion;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /**
     * Checks whether the owner has a new "last hurt by" entity (by comparing
     * timestamps) that is a valid attack target.
     *
     * @return {@code true} if the goal should start targeting the owner's latest attacker
     */
    public boolean canUse() {
        LivingEntity owner = this.minion.getOwner();
        if (owner == null) {
            return false;
        } else {
            this.ownerLastHurtBy = owner.getLastHurtByMob();
            int ts = owner.getLastHurtByMobTimestamp();
            return ts != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
        }
    }

    /**
     * Sets the minion's target to the owner's last attacker and records the
     * current timestamp so the same event isn't reused.
     */
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.minion.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}