package net.necrocraft.world.entity.minion.impl.stray;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FrostyMinion extends StrayMinion {
    /** Duration (in ticks) of the Curse Of The Frost applied on hit. 200 ticks = 10 seconds. */
    private static final int CURSE_DURATION_TICKS = 200;
    /** Amplifier of the Curse Of The Frost applied on hit (0 = level I). */
    private static final int CURSE_AMPLIFIER = 0;

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public FrostyMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * Replaces the base {@link StrayMinion} slowness with a much harsher
     * {@link ModMobEffects#CURSE_OF_THE_FROST}: a deep frost that cripples
     * the target's movement and attack speed and steadily freezes it.
     *
     * @param target the entity the minion hurt
     */
    @Override
    protected void applyHurtEffect(@NotNull Entity target) {
        Objects.requireNonNull(target.asLivingEntity())
                .addEffect(new MobEffectInstance(ModMobEffects.CURSE_OF_THE_FROST, CURSE_DURATION_TICKS, CURSE_AMPLIFIER, true, true, true));
    }
}