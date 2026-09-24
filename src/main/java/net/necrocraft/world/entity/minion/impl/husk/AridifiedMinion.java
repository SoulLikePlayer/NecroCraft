package net.necrocraft.world.entity.minion.impl.husk;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class AridifiedMinion extends HuskMinion{

    static {}

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public AridifiedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable Holder<@NotNull MobEffect> getMinionEffect() {
        return ModMobEffects.CURSE_OF_THE_DROUGHT;
    }
}