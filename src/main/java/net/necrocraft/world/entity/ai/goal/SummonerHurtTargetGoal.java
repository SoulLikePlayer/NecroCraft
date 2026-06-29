package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.EnumSet;

public class SummonerHurtTargetGoal extends TargetGoal {
    private final AbstractMinion minion;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public SummonerHurtTargetGoal(AbstractMinion minion) {
        super(minion, false);
        this.minion = minion;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

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

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity owner = this.minion.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
