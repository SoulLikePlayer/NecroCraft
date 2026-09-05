package net.necrocraft.world.entity.minion.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.entity.minion.AbstractMinion;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Maps vanilla {@link EntityType}s (e.g. {@code ZOMBIE}, {@code SKELETON}) to
 * the minion entity type that should be summoned when that mob type is
 * captured (see the soul-capture mechanic in {@code ModEvent}).
 * <p>
 * Bindings are registered once in the static initializer; additional mappings
 * can be added at runtime via {@link #register(EntityType, Supplier)}.
 */
public class MinionRegistry {
    private static final Map<EntityType<?>, Supplier<? extends  EntityType<? extends @NotNull AbstractMinion>>> REGISTRY =
            new HashMap<>();

    /**
     * Registers a capturable mob type and the minion entity type it should
     * produce when captured.
     *
     * @param capturedType the vanilla (or modded) entity type that can be captured
     * @param minionType   supplier for the minion entity type to summon in its place
     */
    public static void register(EntityType<?> capturedType, Supplier<? extends EntityType<? extends AbstractMinion>> minionType) {
        REGISTRY.put(capturedType, minionType);
    }

    /**
     * @param type the entity type to check
     * @return {@code true} if {@code type} has a registered minion mapping
     */
    public static boolean isCapturable(EntityType<?> type) {
        return REGISTRY.containsKey(type);
    }

    /**
     * Resolves the minion entity type associated with a captured entity type
     * reference, if any.
     *
     * @param capturedType an optional holder reference to the captured entity type
     * @return the corresponding minion {@link EntityType}, or {@code null} if
     *         {@code capturedType} is empty or has no registered mapping
     */
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
        register(EntityTypes.SKELETON, ModEntity.SKELETON_MINION);
        register(EntityTypes.BOGGED, ModEntity.BOGGED_MINION);
        register(EntityTypes.DROWNED, ModEntity.DROWNED_MINION);
        register(EntityTypes.HUSK, ModEntity.HUSK_MINION);
        register(EntityTypes.PARCHED, ModEntity.PARCHED_MINION);
        register(EntityTypes.STRAY, ModEntity.STRAY_MINION);
        register(ModEntity.FROSTY_MINION.get(), ModEntity.FROSTY_MINION);
        register(ModEntity.BLIGHTED_MINION.get(), ModEntity.BLIGHTED_MINION);
    }
}