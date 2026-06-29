package net.necrocraft.world.entity.minion;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ZombieMinion extends AbstractMinion{
    public ZombieMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }
}