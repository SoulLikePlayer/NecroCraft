package net.necrocraft.world.entity.minion.impl.husk;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AridifiedMinion extends HuskMinion{
    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public AridifiedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void applyHurtEffect(@NotNull Entity target) {
        Objects.requireNonNull(target.asLivingEntity())
                .addEffect(new MobEffectInstance(ModMobEffects.CURSE_OF_THE_DROUGHT, 200, 0, true, true, true));
    }
}
