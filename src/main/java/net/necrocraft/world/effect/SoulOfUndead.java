package net.necrocraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SoulOfUndead extends MobEffect {
    public SoulOfUndead(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return tickCount % 2 == 0;
    }
}