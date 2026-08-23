package net.necrocraft.event;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class SoulOfUndeadModEvent {
    private static final Map<UUID, MobEffectInstance> PENDING_SOUL_RESPAWN = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onVillagerTradeAttempt(PlayerInteractEvent.EntityInteract event){
        if(!(event.getTarget() instanceof Villager)) return;
        Player player = event.getEntity();
        if(player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMobTargetChange(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (!(newTarget instanceof Player player)) return;
        if (attacker.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
            if (attacker instanceof WitherBoss) return;
            if (player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onMobEffectRemove(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (event.getEffectInstance() == null) return;
        if (!event.getEffectInstance()
                .getEffect().is(ModMobEffects.SOUL_OF_UNDEAD)) return;

        if (entity.getUseItem().getItem().equals(Items.MILK_BUCKET)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMobEatGoldenApple(LivingEntityUseItemEvent.Finish event){
        ItemStack item = event.getItem();
        LivingEntity entity = event.getEntity();
        Level level = event.getEntity().level();

        if(item.is(Items.GOLDEN_APPLE)
                && entity.hasEffect(MobEffects.WEAKNESS)
                && entity.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)){

            entity.removeEffect(ModMobEffects.SOUL_OF_UNDEAD);
            entity.removeEffect(MobEffects.WEAKNESS);
            entity.removeEffect(MobEffects.ABSORPTION);

            level.playSound(
                    entity,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.ZOMBIE_VILLAGER_CURE,
                    SoundSource.PLAYERS
            );
        }
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (!event.getEffectInstance().getEffect().is(ModMobEffects.SOUL_OF_UNDEAD)) return;

        entity.setData(ModAttachments.SOUL_GAUGE, 0f);
    }

    @SubscribeEvent
    public static void onMobEffectRemoveGauge(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (event.getEffectInstance() == null) return;
        if (!event.getEffectInstance().getEffect().is(ModMobEffects.SOUL_OF_UNDEAD)) return;

        entity.setData(ModAttachments.SOUL_GAUGE, 0f);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Villager)) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) return;

        float gauge = player.getData(ModAttachments.SOUL_GAUGE);
        gauge += 5f;
        if (gauge > 100f) gauge = 100f;
        player.setData(ModAttachments.SOUL_GAUGE, gauge);
        NecroCraft.LOGGER.info("Soul Gauge : {}", gauge);
    }

    @SubscribeEvent
    public static void onPlayerDeathCaptureSoul(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        MobEffectInstance soulEffect = player.getEffect(ModMobEffects.SOUL_OF_UNDEAD);
        if (soulEffect == null) return;

        PENDING_SOUL_RESPAWN.put(player.getUUID(), new MobEffectInstance(
                soulEffect.getEffect(),
                soulEffect.getDuration(),
                soulEffect.getAmplifier(),
                soulEffect.isAmbient(),
                soulEffect.isVisible(),
                soulEffect.showIcon()
        ));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player newPlayer = event.getEntity();
        MobEffectInstance pending = PENDING_SOUL_RESPAWN.remove(newPlayer.getUUID());
        if (pending == null) return;

        newPlayer.addEffect(pending);
    }
}