package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Comparator;
import java.util.List;

public class NemesisHurtTargetGoal extends TargetGoal {
    private static final double SEARCH_RADIUS = 16.0D;

    private final AbstractMinion minion;

    /** @param minion the minion whose target should follow its owner's attacks */
    public NemesisHurtTargetGoal(AbstractMinion minion) {
        super(minion, false);
        this.minion = minion;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!minion.getNemesis()) {
            return false;
        }

        List<LivingEntity> candidates = minion.level().getEntitiesOfClass(
                LivingEntity.class,
                minion.getBoundingBox().inflate(SEARCH_RADIUS),
                entity -> entity != minion
                        && entity.isAlive()
                        && !entity.isInvulnerable()
        );

        LivingEntity nearest = candidates.stream()
                .min(Comparator.comparingDouble(minion::distanceToSqr))
                .orElse(null);

        if (nearest == null) {
            return false;
        }

        this.minion.setTarget(nearest);
        return true;
    }

    @Override
    public void start() {
        super.start();
    }
}