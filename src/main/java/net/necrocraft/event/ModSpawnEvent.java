package net.necrocraft.event;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.entity.monster.skeleton.Echoing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModSpawnEvent {
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntity.ECHOING.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Echoing::checkMonsterSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
    }
}
