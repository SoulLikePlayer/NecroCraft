package net.necrocraft.event;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class SoulOfUndeadModEvent {
    private static final Set<UUID> PROCESSING = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @SubscribeEvent
    public static void onVillagerTradeAttempr(PlayerInteractEvent.EntityInteract event){
        if(!(event.getTarget() instanceof Villager)) return;
        Player player = event.getEntity();
        if(player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) return;
        if (!PROCESSING.add(entity.getUUID())) return;

        try {
            event.setCanceled(true);
            entity.hurt(entity.damageSources().magic(), event.getAmount());
        } finally {
            PROCESSING.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (!entity.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) return;
        if (!event.getSource().is(DamageTypes.INDIRECT_MAGIC) && !event.getSource().is(DamageTypes.MAGIC)) return;
        if (!PROCESSING.add(entity.getUUID())) return;

        try {
            float amount = event.getOriginalDamage();
            var velocityBefore = entity.getDeltaMovement();

            event.setNewDamage(0);
            entity.heal(amount);

            entity.setDeltaMovement(velocityBefore);
        } finally {
            PROCESSING.remove(entity.getUUID());
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
    public static void onMobEffectRemove(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        if (event.getEffectInstance() == null) return;
        if (!event.getEffectInstance().getEffect().is(ModMobEffects.SOUL_OF_UNDEAD)) return;

        if (entity.getUseItem().getItem().equals(Items.MILK_BUCKET)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        if (!original.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) return;

        MobEffectInstance oldInstance = original.getEffect(ModMobEffects.SOUL_OF_UNDEAD);
        if (oldInstance == null) return;

        Player newPlayer = event.getEntity();
        newPlayer.addEffect(new MobEffectInstance(oldInstance));
    }
}