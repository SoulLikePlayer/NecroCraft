package net.necrocraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CurseOfTheSea extends MobEffect {

    private static final float BASE_HEAL_IN_WATER = 1.0f;
    private static final float BASE_HEAL_IN_LAND = 0.5f;

    protected CurseOfTheSea(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel serverLevel, @NotNull LivingEntity mob, int amplifier) {
        if (mob.getAirSupply() < mob.getMaxAirSupply()) {
            mob.setAirSupply(mob.getMaxAirSupply());
        }

        if (!mob.isDeadOrDying() && mob.getHealth() < mob.getMaxHealth()) {
            boolean inWater = mob.isInWater() || mob.isUnderWater();

            if (inWater) {
                float healAmount = BASE_HEAL_IN_WATER + amplifier;
                mob.heal(healAmount);
            } else {
                mob.heal(BASE_HEAL_IN_LAND);
            }
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        int period = Math.max(50 >> amplifier, 5);
        return tickCount % period == 0;
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity mob, int amplifier) {
        super.onEffectAdded(mob, amplifier);
        mob.setAirSupply(mob.getMaxAirSupply());
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity mob, int amplifier) {
        super.onEffectStarted(mob, amplifier);
        mob.setAirSupply(mob.getMaxAirSupply());
    }
}