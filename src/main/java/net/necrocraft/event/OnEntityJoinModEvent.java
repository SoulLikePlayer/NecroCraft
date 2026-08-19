package net.necrocraft.event;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.effect.ModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class OnEntityJoinModEvent {
    @SubscribeEvent
    public static void onVillagerJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Villager villager)) return;

        villager.goalSelector.addGoal(1, new AvoidEntityGoal<>(
                villager,
                Player.class,
                p -> p.hasEffect(ModMobEffects.SOUL_OF_UNDEAD),
                8.0F,
                1.2D,
                1.4D,
                EntitySelector.NO_SPECTATORS
        ));
    }

    @SubscribeEvent
    public static void onGolemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof IronGolem golem)) return;

        golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                golem,
                Player.class,
                10,
                true,
                false,
                (p, _) -> p.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)
        ));
    }
}
