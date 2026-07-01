package net.necrocraft.world.entity.minion;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.necrocraft.world.entity.ModEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class MinionRegistry {
    private static final Map<EntityType<?>, Supplier<? extends  EntityType<? extends @NotNull AbstractMinion>>> REGISTRY =
            new HashMap<>();

    public static void register(EntityType<?> capturedType, Supplier<? extends EntityType<? extends AbstractMinion>> minionType) {
        REGISTRY.put(capturedType, minionType);
    }

    public static boolean isCapturable(EntityType<?> type) {
        return REGISTRY.containsKey(type);
    }

    @Nullable
    public static EntityType<? extends @NotNull AbstractMinion> getMinionFor(Optional<Holder.Reference<@NotNull EntityType<?>>> capturedType) {
        if (capturedType.isEmpty()) {
            return null;
        }
        EntityType<?> type = capturedType.get().value();
        Supplier<? extends EntityType<? extends @NotNull AbstractMinion>> supplier = REGISTRY.get(type);
        return supplier != null ? supplier.get() : null;
    }

    static {
        register(EntityTypes.ZOMBIE, ModEntity.ZOMBIE_MINION);
    }
}
