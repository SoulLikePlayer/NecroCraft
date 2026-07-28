package net.necrocraft.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * A {@link Goal} decorator that wraps another goal (the {@code delegate}) and
 * only allows it to run while an external {@code condition} holds true.
 * <p>
 * All lifecycle calls ({@link #start()}, {@link #stop()}, {@link #tick()}) are
 * forwarded directly to the delegate; only {@link #canUse()} and
 * {@link #canContinueToUse()} are additionally gated by the condition.
 */
public class GatedGoal extends Goal {
    private final Goal delegate;
    private final BooleanSupplier condition;

    /**
     * @param delegate  the wrapped goal whose behavior is gated
     * @param condition supplier evaluated to decide whether the delegate is allowed to run
     */
    public GatedGoal(@NotNull Goal delegate, @NotNull BooleanSupplier condition) {
        this.delegate = delegate;
        this.condition = condition;
        this.setFlags(delegate.getFlags());
    }

    /**
     * @return {@code true} if the condition currently holds and the delegate wants to start
     */
    @Override
    public boolean canUse() {
        return this.condition.getAsBoolean() && this.delegate.canUse();
    }

    /**
     * @return {@code true} if the condition currently holds and the delegate wants to continue
     */
    @Override
    public boolean canContinueToUse() {
        return this.condition.getAsBoolean() && this.delegate.canContinueToUse();
    }

    /** Starts the delegate goal. */
    @Override
    public void start() {
        this.delegate.start();
    }

    /** Stops the delegate goal. */
    @Override
    public void stop() {
        this.delegate.stop();
    }

    /** Ticks the delegate goal. */
    @Override
    public void tick() {
        this.delegate.tick();
    }

    /** @return whether the delegate goal requires a tick every game tick */
    @Override
    public boolean requiresUpdateEveryTick() {
        return this.delegate.requiresUpdateEveryTick();
    }
}