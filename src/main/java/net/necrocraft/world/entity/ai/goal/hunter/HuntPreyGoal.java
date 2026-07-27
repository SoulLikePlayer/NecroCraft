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

public class HuntPreyGoal extends TargetGoal {
    private final AbstractMinion minion;
    private final float range;
    private LivingEntity prey;
    private int cooldown;

    public HuntPreyGoal(AbstractMinion minion, float range) {
        super(minion, false);
        this.minion = minion;
        this.range = range;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

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

    @Override
    public void start() {
        this.mob.setTarget(this.prey);
        super.start();
    }
}