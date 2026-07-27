package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class GatedGoal extends Goal {
    private final Goal delegate;
    private final BooleanSupplier condition;

    public GatedGoal(@NotNull Goal delegate, @NotNull BooleanSupplier condition) {
        this.delegate = delegate;
        this.condition = condition;
        this.setFlags(delegate.getFlags());
    }

    @Override
    public boolean canUse() {
        return this.condition.getAsBoolean() && this.delegate.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.condition.getAsBoolean() && this.delegate.canContinueToUse();
    }

    @Override
    public void start() {
        this.delegate.start();
    }

    @Override
    public void stop() {
        this.delegate.stop();
    }

    @Override
    public void tick() {
        this.delegate.tick();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return this.delegate.requiresUpdateEveryTick();
    }
}