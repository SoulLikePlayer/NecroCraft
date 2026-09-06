package net.necrocraft.world.effect.curse;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

public class CurseOfTheDrought extends MobEffect {
    public CurseOfTheDrought(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull ServerLevel serverLevel, @NotNull LivingEntity mob, int amplification) {
        if (mob instanceof Player player) {
            player.causeFoodExhaustion(0.1F * (float)(amplification + 1));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return true;
    }

    public static boolean blocksHealing(@NotNull LivingEntity entity) {
        return entity.hasEffect(ModMobEffects.CURSE_OF_THE_DROUGHT);
    }
}
