package net.necrocraft.world.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SoulOfUndeadAbilities {

    private static final float HEAL_AMOUNT = 6.0F;
    private static final float HEAL_SOUL_COST = 15.0F;
    private static final int HEAL_COOLDOWN_TICKS = 100;

    private static final float EMPOWER_RADIUS = 12.0F;
    private static final int EMPOWER_DURATION_TICKS = 200;
    private static final int EMPOWER_AMPLIFIER = 0;
    private static final float EMPOWER_SOUL_COST = 25.0F;
    private static final int EMPOWER_COOLDOWN_TICKS = 200;

    private static final Map<UUID, Long> LAST_HEAL_TICK = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_EMPOWER_TICK = new ConcurrentHashMap<>();

    private SoulOfUndeadAbilities() {
    }

    public static void handleSelfHealRequest(ServerPlayer player) {
        if (!player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
            return;
        }
        if (onCooldown(player, LAST_HEAL_TICK, HEAL_COOLDOWN_TICKS)) {
            return;
        }
        if (trySpendSoulGauge(player, HEAL_SOUL_COST)) {
            return;
        }

        player.heal(HEAL_AMOUNT);
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ZOMBIE_VILLAGER_CURE,
                SoundSource.PLAYERS,
                1.0F, 1.4F
        );

        LAST_HEAL_TICK.put(player.getUUID(), player.level().getGameTime());
    }

    public static void handleEmpowerMinionsRequest(ServerPlayer player) {
        if (!player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
            return;
        }
        if (onCooldown(player, LAST_EMPOWER_TICK, EMPOWER_COOLDOWN_TICKS)) {
            return;
        }

        List<AbstractMinion> minions = player.level().getEntitiesOfClass(
                AbstractMinion.class,
                player.getBoundingBox().inflate(EMPOWER_RADIUS),
                minion -> Objects.requireNonNull(minion.getOwner()).is(player)
        );

        if (minions.isEmpty()) {
            return;
        }
        if (trySpendSoulGauge(player, EMPOWER_SOUL_COST)) {
            return;
        }

        for (AbstractMinion minion : minions) {
            minion.addEffect(new MobEffectInstance(MobEffects.SPEED, EMPOWER_DURATION_TICKS, EMPOWER_AMPLIFIER, false, true, true));
            minion.addEffect(new MobEffectInstance(MobEffects.STRENGTH, EMPOWER_DURATION_TICKS, EMPOWER_AMPLIFIER, false, true, true));
        }

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                1.0F, 0.7F
        );

        LAST_EMPOWER_TICK.put(player.getUUID(), player.level().getGameTime());
    }

    private static boolean onCooldown(ServerPlayer player, Map<UUID, Long> lastUse, int cooldownTicks) {
        Long last = lastUse.get(player.getUUID());
        return last != null && (player.level().getGameTime() - last) < cooldownTicks;
    }

    private static boolean trySpendSoulGauge(ServerPlayer player, float cost) {
        float gauge = player.getData(ModAttachments.SOUL_GAUGE);
        if (gauge < cost) {
            NecroCraft.LOGGER.debug("{} tried to use an ability but only had {} Soul Gauge (needs {})",
                    player.getGameProfile().name(), gauge, cost);
            return true;
        }
        player.setData(ModAttachments.SOUL_GAUGE, gauge - cost);
        return false;
    }
}