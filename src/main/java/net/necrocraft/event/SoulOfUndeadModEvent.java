package net.necrocraft.event;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class SoulOfUndeadModEvent {

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
        if (!event.getEffectInstance().getEffect().is(ModMobEffects.SOUL_OF_UNDEAD)) return;

        if (entity.getUseItem().getItem().equals(Items.MILK_BUCKET)) {
            event.setCanceled(true);
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
}