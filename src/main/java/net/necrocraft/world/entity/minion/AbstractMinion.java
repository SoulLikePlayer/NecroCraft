package net.necrocraft.world.entity.minion;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Abstract class who define what is a minion in NecroCraft.
 * A minion is a summonable mob define by a summoner
 */
public class AbstractMinion extends PathfinderMob {

    protected static final EntityDataAccessor<@NotNull Optional<EntityReference<@NotNull LivingEntity>>> DATA_SUMMONER_UUID_ID =
            SynchedEntityData.defineId(AbstractMinion.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    protected AbstractMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * Define all the data that sync at the creation of the mob
     *
     * @param entityData : the builder of the data that need to be defined
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SUMMONER_UUID_ID, Optional.empty());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference<@NotNull LivingEntity> summoner = this.getSummonerReference();
        EntityReference.store(summoner, output, "summoner");
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<@NotNull LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "summoner", this.level());
        if (owner != null) {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.of(owner));
        }
    }

    private EntityReference<@NotNull LivingEntity> getSummonerReference() {
        return this.entityData
                .get(DATA_SUMMONER_UUID_ID)
                .orElse(null);
    }
}
