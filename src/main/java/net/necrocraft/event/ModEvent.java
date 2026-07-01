package net.necrocraft.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.MinionRegistry;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.component.SoulData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModEvent {

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        EntityType<?> killedType = event.getEntity().getType();
        if (!MinionRegistry.isCapturable(killedType)) return;

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof Player player)) return;

        ItemStack offhandItem = player.getOffhandItem();
        if (!offhandItem.is(ModItems.SOUL_TOTEM.get())) return;

        if (offhandItem.has(ModDataComponents.SOUL_DATA.get())) return;

        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(killedType);
        offhandItem.set(ModDataComponents.SOUL_DATA.get(), new SoulData(entityId));
    }
}