package net.necrocraft.world.entity.minion.impl.stray;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

/**
 * Replaces the base {@link StrayMinion} slowness with a much harsher
 * {@link ModMobEffects#CURSE_OF_THE_FROST}: a deep frost that cripples
 * the target's movement and attack speed and steadily freezes it.
 */
public class FrostyMinion extends StrayMinion {

    static {
    }

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public FrostyMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable Holder<@NotNull MobEffect> getMinionEffect() {
        return ModMobEffects.CURSE_OF_THE_FROST;
    }
}