package net.necrocraft.world.entity.minion.registry;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MinionEvolutions {
    private static final Map<EntityType<?>, List<MinionEvolution>> EVOLUTIONS = new HashMap<>();

    public static void register(EntityType<?> from, MinionEvolution evolution) {
        EVOLUTIONS.computeIfAbsent(from, k -> new ArrayList<>()).add(evolution);
    }

    public static List<MinionEvolution> getEvolutions(EntityType<?> from) {
        return EVOLUTIONS.getOrDefault(from, List.of());
    }
}