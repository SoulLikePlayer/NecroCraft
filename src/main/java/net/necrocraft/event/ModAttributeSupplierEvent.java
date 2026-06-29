package net.necrocraft.event;

import net.minecraft.world.entity.monster.zombie.Zombie;
import net.necrocraft.core.NecroCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import static net.necrocraft.world.entity.ModEntity.ZOMBIE_MINION;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModAttributeSupplierEvent {

    @SubscribeEvent
    public static void createDefaultAttributes(EntityAttributeCreationEvent event){
        event.put(ZOMBIE_MINION.get(), Zombie.createAttributes().build());
    }
}
