package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.EnumSet;

public class SummonerHurtByTargetGoal extends TargetGoal {
    private final AbstractMinion minion;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public SummonerHurtByTargetGoal(AbstractMinion minion) {
        super(minion, false);
        this.minion = minion;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

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

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.minion.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
