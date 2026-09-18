package net.necrocraft.world.entity.minion.impl.husk;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IgnitedMinion extends HuskMinion{
    private static final float FIRE_SECOND = 5;

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public IgnitedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void applyHurtEffect(@NotNull Entity target) {
        Objects.requireNonNull(target.asLivingEntity())
                .igniteForSeconds(FIRE_SECOND);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
