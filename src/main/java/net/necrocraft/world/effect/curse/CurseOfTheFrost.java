package net.necrocraft.world.effect.curse;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.stray.StrayMinion;
import org.jetbrains.annotations.NotNull;

/**
 * Debuff inflicted by the {@link net.necrocraft.world.entity.minion.impl.stray.FrostyMinion}
 * on hit: a deep, magical frost that cripples the target far harder than
 * vanilla {@link MobEffects#SLOWNESS}, on top of freezing it like standing
 * in powder snow.
 * <p>
 * Unlike {@link StrayMinion}'s base slowness, this effect drastically cuts
 * both movement and attack speed and steadily pushes the victim's freeze
 * gauge up while active, so it will start taking freeze damage even outside
 * of cold biomes.
 */
public class CurseOfTheFrost extends MobEffect {

    private static final Identifier MOVEMENT_SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(NecroCraft.MODID, "effect.curse_of_the_frost.movement_speed");
    private static final Identifier ATTACK_SPEED_MODIFIER_ID =
            Identifier.fromNamespaceAndPath(NecroCraft.MODID, "effect.curse_of_the_frost.attack_speed");

    /** Movement speed penalty per amplifier level (multiplicative, stacks with {@link AttributeModifier.Operation#ADD_MULTIPLIED_TOTAL}). */
    private static final double MOVEMENT_SPEED_PENALTY_PER_LEVEL = -0.30D;
    /** Attack speed penalty per amplifier level. */
    private static final double ATTACK_SPEED_PENALTY_PER_LEVEL = -0.25D;
    /** How many extra freeze ticks are added to the victim every second the curse is active. */
    private static final int FREEZE_TICKS_PER_SECOND = 6;

    public CurseOfTheFrost(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_ID,
                MOVEMENT_SPEED_PENALTY_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID,
                ATTACK_SPEED_PENALTY_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /**
     * Runs once a second while the curse is active: pushes the victim's
     * freeze gauge up like it was standing in powder snow, regardless of
     * biome temperature.
     *
     * @param entity    the cursed entity
     * @param amplifier the effect's amplifier (0 = level I)
     * @return {@code true} to keep the effect running
     */
    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        int gain = FREEZE_TICKS_PER_SECOND * (amplification + 1);
        mob.setTicksFrozen(Math.min(mob.getTicksFrozen() + gain, mob.getTicksRequiredToFreeze()));
        return true;
    }

    /**
     * @param duration  ticks remaining on the effect instance
     * @param amplifier the effect's amplifier
     * @return {@code true} once a second (every 20 ticks), so {@link #applyEffectTick} runs at a steady pace
     */
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}