package net.necrocraft.world.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.SkeletonMinion;
import net.necrocraft.world.entity.minion.ZombieMinion;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Central registry of the mod's custom {@link EntityType}s.
 * <p>
 * Entity types are declared as {@link DeferredRegister} entries so they are
 * registered lazily once the underlying registry is available, as required
 * by NeoForge's mod loading lifecycle.
 */
public class ModEntity {

    /** Deferred register holding all entity types contributed by this mod. */
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(NecroCraft.MODID);

    /** The {@link EntityType} for {@link ZombieMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull ZombieMinion>> ZOMBIE_MINION = ENTITY_TYPES.registerEntityType(
            "zombie_minions",
            ZombieMinion::new,
            MobCategory.MISC
    );

    /** The {@link EntityType} for {@link SkeletonMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull SkeletonMinion>> SKELETON_MINION = ENTITY_TYPES.registerEntityType(
            "skeleton_minions",
            SkeletonMinion::new,
            MobCategory.MISC
    );
}