package net.necrocraft.world.entity.minion.impl.bogged;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.necrocraft.world.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlightedMinion extends BoggedMinion{

    static {
        SYNCED_BONUS.add(ModItems.VIAL_WITHERING_VENOM_BONUS_ITEM);

        SYNCED_BONUS.remove(ModItems.VIAL_PUTRID_VENOM_BONUS_ITEM);
    }

    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    public BlightedMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void applyHurtEffect(@NotNull Entity target) {
        Objects.requireNonNull(target.asLivingEntity())
                .addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0, true, true, true));
    }
}
