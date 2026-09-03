package net.necrocraft.world.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.impl.*;
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
public class  ModEntity {

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

    /** The {@link EntityType} for {@link BoggedMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull BoggedMinion>> BOGGED_MINION = ENTITY_TYPES.registerEntityType(
            "bogged_minions",
            BoggedMinion::new,
            MobCategory.MISC
    );

    /** The {@link EntityType} for {@link DrownedMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull DrownedMinion>> DROWNED_MINION = ENTITY_TYPES.registerEntityType(
            "drowned_minions",
            DrownedMinion::new,
            MobCategory.MISC
    );

    /** The {@link EntityType} for {@link HuskMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull HuskMinion>> HUSK_MINION = ENTITY_TYPES.registerEntityType(
            "husk_minions",
            HuskMinion::new,
            MobCategory.MISC
    );

    /** The {@link EntityType} for {@link ParchedMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull ParchedMinion>> PARCHED_MINION = ENTITY_TYPES.registerEntityType(
            "parched_minions",
            ParchedMinion::new,
            MobCategory.MISC
    );

    /** The {@link EntityType} for {@link StrayMinion}, classified under {@link MobCategory#MISC}. */
    public static final Supplier<EntityType<@NotNull StrayMinion>> STRAY_MINION = ENTITY_TYPES.registerEntityType(
            "stray_minions",
            StrayMinion::new,
            MobCategory.MISC
    );
}