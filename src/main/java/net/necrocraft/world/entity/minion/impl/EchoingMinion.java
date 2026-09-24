package net.necrocraft.world.entity.minion.impl;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class EchoingMinion extends SkeletonMinion {

    static {
        SYNCED_BONUS.add(ModItems.ECHOING_BONE_BONUS_ITEM);
    }

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public EchoingMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable Holder<@NotNull MobEffect> getMinionEffect() {
        return MobEffects.DARKNESS;
    }
}