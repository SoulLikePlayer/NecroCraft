package net.necrocraft.event;

import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.necrocraft.core.NecroCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import static net.necrocraft.world.entity.ModEntity.SKELETON_MINION;
import static net.necrocraft.world.entity.ModEntity.ZOMBIE_MINION;

/**
 * Supplies the default {@link net.minecraft.world.entity.ai.attributes.AttributeMap}
 * for the mod's minion entity types.
 * <p>
 * Minions currently reuse the vanilla base attributes of the mob they are
 * derived from ({@link Zombie} and {@link Skeleton}) rather than defining
 * a bespoke attribute set.
 */
@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModAttributeSupplierEvent {

    /**
     * Registers the default attribute suppliers for {@code ZOMBIE_MINION} and
     * {@code SKELETON_MINION}, mirroring vanilla {@link Zombie} and {@link Skeleton} attributes.
     *
     * @param event the NeoForge event used to register attribute suppliers for entity types
     */
    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(ZOMBIE_MINION.get(), Zombie.createAttributes().build());
        event.put(SKELETON_MINION.get(), Skeleton.createAttributes().build());
    }
}