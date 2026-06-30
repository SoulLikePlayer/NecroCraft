package net.necrocraft.world.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.ZombieMinion;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModEntity {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(NecroCraft.MODID);

    public static final Supplier<EntityType<@NotNull ZombieMinion>> ZOMBIE_MINION = ENTITY_TYPES.registerEntityType(
            "zombie_minions",
            ZombieMinion::new,
            MobCategory.MISC
    );
}
