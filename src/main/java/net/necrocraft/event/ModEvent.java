package net.necrocraft.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.necrocraft.world.entity.minion.registry.MinionRegistry;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.component.SoulData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Handles the "soul capture" mechanic: when a capturable mob dies at the hands
 * of a player holding an empty {@code Soul Totem} in their offhand, the mob's
 * type is stored on the totem so it can later be used to summon a minion of
 * that type.
 */
@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModEvent {

    /**
     * Listens for any living entity's death and, if the killed entity is
     * registered as capturable (see {@link MinionRegistry#isCapturable(EntityType)}),
     * the killer is a player, and that player holds an unbound {@code Soul Totem}
     * in their offhand, stamps the totem with the killed entity's type as
     * {@link SoulData} and plays a capture effect at the player's position.
     * <p>
     * Does nothing on the client side, for non-capturable mobs, for non-player
     * attackers, for players not holding a soul totem in their offhand, or if
     * the totem already carries soul data.
     *
     * @param event the death event fired for the killed entity
     */
    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        EntityType<?> killedType = event.getEntity().getType();
        if (!MinionRegistry.isCapturable(killedType)) return;

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player)) return;

        ItemStack offhandItem = player.getOffhandItem();
        if (!offhandItem.is(ModItems.SOUL_TOTEM.get())) return;

        if (offhandItem.has(ModDataComponents.SOUL_DATA.get())) return;

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(killedType);
        offhandItem.set(ModDataComponents.SOUL_DATA.get(), new SoulData(entityId));

        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            spawnSoulCaptureEffect(serverLevel, player.getX(), player.getY() + 1.0, player.getZ());
        }
    }

    /**
     * Plays the particle and sound feedback for a successful soul capture
     * (soul, smoke and soul-fire particles, plus totem/soul-escape sounds)
     * at the given position.
     *
     * @param level the server level in which to spawn particles and play sounds
     * @param x     the world X coordinate of the effect
     * @param y     the world Y coordinate of the effect
     * @param z     the world Z coordinate of the effect
     */
    private static void spawnSoulCaptureEffect(ServerLevel level, double x, double y, double z) {
        level.sendParticles(
                ParticleTypes.SOUL,
                x, y, z,
                80,
                0.4, 0.6, 0.4,
                0.2
        );

        level.sendParticles(
                ParticleTypes.LARGE_SMOKE,
                x, y, z,
                30,
                0.5, 0.7, 0.5,
                0.05
        );

        level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z,
                25,
                0.2, 0.4, 0.2,
                0.05
        );

        level.playSound(
                null,
                x, y, z,
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        level.playSound(
                null,
                x, y, z,
                SoundEvents.SOUL_ESCAPE,
                SoundSource.PLAYERS,
                1.0F,
                0.7F + level.getRandom().nextFloat() * 0.3F
        );
    }
}