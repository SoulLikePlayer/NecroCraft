package net.necrocraft.world.entity.minion.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.item.ModItems;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinionEvolutionBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinionEvolutionBootstrap.class);
    private static boolean registered = false;

    private MinionEvolutionBootstrap() {}

    public static synchronized void register() {
        if (registered) {
            return;
        }
        registered = true;

        MinionEvolutions.register(EntityTypes.ZOMBIE,
                new MinionEvolution(EntityTypes.DROWNED, ModItems.NECROTIC_POWDER.get(), 20, 5));
        MinionEvolutions.register(EntityTypes.ZOMBIE,
                new MinionEvolution(EntityTypes.HUSK, ModItems.NECROTIC_POWDER.get(), 20, 5));

        MinionEvolutions.register(EntityTypes.SKELETON,
                new MinionEvolution(EntityTypes.STRAY, ModItems.NECROTIC_POWDER.get(), 20, 5));
        MinionEvolutions.register(EntityTypes.SKELETON,
                new MinionEvolution(EntityTypes.BOGGED, ModItems.NECROTIC_POWDER.get(), 20, 5));
        MinionEvolutions.register(EntityTypes.SKELETON,
                new MinionEvolution(EntityTypes.PARCHED, ModItems.NECROTIC_POWDER.get(), 20, 5));
        MinionEvolutions.register(EntityTypes.SKELETON,
                new MinionEvolution(ModEntity.ECHOING_MINION.get(), ModItems.NECROTIC_POWDER.get(), 20, 5));

        MinionEvolutions.register(EntityTypes.STRAY,
                new MinionEvolution(ModEntity.FROSTY_MINION.get(), ModItems.NECROTIC_POWDER.get(), 45, 15));

        MinionEvolutions.register(EntityTypes.BOGGED,
                new MinionEvolution(ModEntity.BLIGHTED_MINION.get(), ModItems.NECROTIC_POWDER.get(), 45, 15));

        MinionEvolutions.register(EntityTypes.HUSK,
                new MinionEvolution(ModEntity.ARIDIFIED_MINION.get(), ModItems.NECROTIC_POWDER.get(), 45, 15));
    }
}