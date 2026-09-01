package net.necrocraft.world.entity.minion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.util.AdvancementUtil;
import net.necrocraft.world.entity.ai.goal.*;
import net.necrocraft.world.entity.ai.goal.farmer.FarmCropsGoal;
import net.necrocraft.world.entity.ai.goal.farmer.PlantSeedsGoal;
import net.necrocraft.world.entity.ai.goal.farmer.TillFarmlandGoal;
import net.necrocraft.world.entity.ai.goal.hunter.HuntPreyGoal;
import net.necrocraft.world.entity.minion.impl.SkeletonMinion;
import net.necrocraft.world.entity.minion.impl.ZombieMinion;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import net.necrocraft.world.item.bonus.BonusUtil;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Abstract base class defining what a minion is in NecroCraft: a summonable
 * mob owned by a summoner, carrying its own 27-slot inventory, an
 * {@link net.necrocraft.world.item.bonus.AbstractBonusItem} loadout that
 * unlocks behaviors (farming, hunting, storing, sedentary mode...), and the
 * ability to teleport back to its owner when left too far behind.
 * <p>
 * Concrete subclasses (e.g. {@link ZombieMinion}, {@link SkeletonMinion})
 * only need to supply mob-specific sounds & specific behavior; all shared behavior (goals,
 * inventory, ownership, persistence) lives here.
 * <p>
 * Class layout, top to bottom: constants/fields, construction, AI goals,
 * synced data, persistence, ownership, teleportation, container interaction
 * (chest UI tracking), the bonus-trigger system, combat/lifecycle, the
 * {@link Container} implementation backing the minion's inventory, and
 * finally player interaction.
 */
public class AbstractMinion extends PathfinderMob implements OwnableEntity, Container, ContainerUser {
    public static final ArrayList<DeferredItem<@NotNull AbstractBonusItem>> SYNCED_BONUS = new ArrayList<>();

    /**
     * Squared distance to the owner beyond which the minion attempts to teleport to them.
     */
    public static final int TELEPORT_WHEN_DISTANCE_IS_SQ = 144;
    /**
     * Synced reference to the living entity that owns/summoned this minion, if any.
     */
    protected static final EntityDataAccessor<@NotNull Optional<EntityReference<@NotNull LivingEntity>>> DATA_SUMMONER_UUID_ID =
            SynchedEntityData.defineId(AbstractMinion.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final int MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 1;
    private static final int INVENTORY_SIZE = 27;
    /**
     * The minion's own carried inventory.
     */
    private final NonNullList<@NotNull ItemStack> inventoryItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    public int lastCombatTick = 0;
    /**
     * Identifiers of the bonus items currently equipped on this minion, driving its optional behaviors.
     */
    private List<Identifier> bonuses = new ArrayList<>();
    /**
     * Position of the container currently visually opened by this minion, if any (see {@link StoreItemsInContainerGoal}).
     */
    private @Nullable BlockPos openedChestPos;

    /**
     * Synced flag: whether this minion has been corrupted by a {@link net.necrocraft.world.item.equipment.NemesisShard}.
     * Kept in {@link SynchedEntityData} (rather than a plain field) so the client
     * picks up the change immediately, e.g. to swap render textures in real time.
     */
    protected static final EntityDataAccessor<Boolean> DATA_NEMESIS_ID =
            SynchedEntityData.defineId(AbstractMinion.class, EntityDataSerializers.BOOLEAN);
    /** Chance for a non-corrupted minion to drop a single {@link ModItems#NEMESIS_SHARD} on death. */
    private static final float NEMESIS_SHARD_DROP_CHANCE = 0.15F;
    /** Maximum number of {@link ModItems#NEMESIS_SHARD} a corrupted ({@link #getNemesis()}) minion can drop on death. */
    private static final int MAX_NEMESIS_SHARD_DROPS = 5;


    /**
     * @param type  the entity type this minion is instantiated from
     * @param level the level the minion is created in
     */
    protected AbstractMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * Fires {@link BonusTrigger#ON_SPAWN} once the minion is actually placed
     * into the world, then defers to vanilla spawn finalization.
     */
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level,
                                                  @NotNull DifficultyInstance difficulty,
                                                  @NotNull EntitySpawnReason spawnReason,
                                                  @Nullable SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnReason, groupData);

        this.setData(ModAttachments.SOUL_GAUGE, 0f);
        this.setNemesis(false);

        fireTrigger(BonusTrigger.ON_SPAWN);
        fireSyncedTrigger(BonusTrigger.ON_SPAWN);
        return result;
    }

    /**
     * Registers this minion's AI goals and targeting goals on top of the
     * vanilla ones. Most goals are wrapped in a {@link GatedGoal} so they
     * only activate when relevant bonuses are equipped or the minion is not
     * {@link #isSedentary() sedentary}:
     * <ul>
     *     <li>Melee attack, following the owner, and combat-assist targeting
     *     are disabled while sedentary. Following the owner is also disabled
     *     while the minion is {@link #getNemesis() corrupted}.</li>
     *     <li>Auto-planting and auto-tilling require the corresponding bonus
     *     (see {@link #canAutoPlant()}, {@link #canAutoTill()}).</li>
     *     <li>Crop farming and storing items in containers run unconditionally
     *     (their own {@code canUse()} checks handle relevance).</li>
     * </ul>
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new GatedGoal(new MeleeAttackGoal(this, 1.0D, true), () -> !this.isSedentary()));
        this.goalSelector.addGoal(2, new FarmCropsGoal(this, 1.0D, 6));
        this.goalSelector.addGoal(3, new GatedGoal(new PlantSeedsGoal(this, 1.0D, 6), this::canAutoPlant));
        this.goalSelector.addGoal(4, new GatedGoal(new TillFarmlandGoal(this, 1.0D, 6), this::canAutoTill));
        this.goalSelector.addGoal(5, new StoreItemsInContainerGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new GatedGoal(new FollowSummonerGoal(this, 1.0F, 10.0F, 2.0F), () -> !this.isSedentary() && !this.getNemesis()));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(0, new NemesisHurtTargetGoal(this));
        this.targetSelector.addGoal(1, new GatedGoal(new SummonerHurtByTargetGoal(this), () -> !this.isSedentary()));
        this.targetSelector.addGoal(2, new GatedGoal(new SummonerHurtTargetGoal(this), () -> !this.isSedentary()));
        this.targetSelector.addGoal(3, new GatedGoal(new HuntPreyGoal(this, 16.0F), () -> !this.isSedentary()));
    }


    /**
     * Defines all the data that is synced at the creation of the mob.
     *
     * @param entityData the builder used to declare synced data entries
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SUMMONER_UUID_ID, Optional.empty());
        entityData.define(DATA_NEMESIS_ID, false);
    }


    /**
     * Writes this minion's owner reference, bonus list and inventory contents
     * to persistent storage.
     *
     * @param output the save-data sink to write to
     */
    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference<@NotNull LivingEntity> summoner = this.getOwnerReference();
        EntityReference.store(summoner, output, "summoner");
        output.store("bonuses", Identifier.CODEC.listOf(), this.bonuses);
        output.putBoolean("isNemesis", this.getNemesis());
        ContainerHelper.saveAllItems(output, this.inventoryItems);
    }

    /**
     * Restores this minion's owner reference, bonus list and inventory
     * contents from persistent storage.
     *
     * @param input the save-data source to read from
     */
    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<@NotNull LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "summoner", this.level());
        if (owner != null) {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.of(owner));
        }
        this.bonuses = new ArrayList<>(input.read("bonuses", Identifier.CODEC.listOf()).orElse(List.of()));
        this.setNemesis(input.getBooleanOr("isNemesis", false));
        this.inventoryItems.clear();
        ContainerHelper.loadAllItems(input, this.inventoryItems);
    }


    /**
     * @return the synced reference to this minion's owner, or {@code null} if it has none
     */
    public EntityReference<@NotNull LivingEntity> getOwnerReference() {
        return this.entityData
                .get(DATA_SUMMONER_UUID_ID)
                .orElse(null);
    }

    /**
     * @param entity the entity to check
     * @return {@code true} if {@code entity} is this minion's owner
     */
    public boolean isSummonedBy(LivingEntity entity) {
        return entity == this.getOwner();
    }

    /**
     * Sets (or clears) this minion's owner.
     *
     * @param owner the new owner, or {@code null} to remove ownership
     */
    public void setOwner(LivingEntity owner) {
        if (owner != null) {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.of(EntityReference.of(owner)));
        } else {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.empty());
        }
    }


    /**
     * Attempts to teleport this minion to a random walkable position near
     * its owner. Does nothing if the minion has no owner. Fires
     * {@link BonusTrigger#ON_TELEPORT} whenever the teleport actually happens.
     */
    public void tryToTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            boolean teleported = this.teleportToAroundBlockPos(owner.blockPosition());
            if (teleported) {
                fireTrigger(BonusTrigger.ON_TELEPORT);
                fireSyncedTrigger(BonusTrigger.ON_TELEPORT);
            }
        }
    }

    /**
     * @return {@code true} if the minion has an owner, is not sedentary, is
     * not {@link #getNemesis() corrupted}, and is at least
     * {@link #TELEPORT_WHEN_DISTANCE_IS_SQ} (squared blocks) away from them
     */
    public boolean shouldTryTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        return owner != null && !this.isSedentary() && !this.getNemesis()
                && this.distanceToSqr(this.getOwner()) >= (double) 144.0F;
    }

    /**
     * Tries up to 10 random offsets around {@code targetPos} (within
     * {@link #MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING} to
     * {@link #MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING} blocks
     * horizontally and {@link #MAX_VERTICAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING}
     * blocks vertically) and teleports to the first valid one found.
     *
     * @param targetPos the position to teleport around (typically the owner's position)
     * @return {@code true} if a valid position was found and the teleport happened
     */
    private boolean teleportToAroundBlockPos(BlockPos targetPos) {
        for (int attempt = 0; attempt < 10; ++attempt) {
            int xd = this.random.nextIntBetweenInclusive(-3, 3);
            int zd = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xd) >= 2 || Math.abs(zd) >= 2) {
                int yd = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(targetPos.getX() + xd, targetPos.getY() + yd, targetPos.getZ() + zd)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Teleports the minion to the given coordinates if they are a valid
     * teleport destination.
     *
     * @param x target world X coordinate
     * @param y target world Y coordinate
     * @param z target world Z coordinate
     * @return {@code true} if the teleport happened, {@code false} if the position was invalid
     */
    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.snapTo((double) x + (double) 0.5F, y, (double) z + (double) 0.5F, this.getYRot(), this.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    /**
     * Checks whether {@code pos} is a safe teleport target: it must be
     * walkable, not sitting on leaves (unless the minion can fly to its
     * owner), and free of collisions once the minion's bounding box is moved there.
     *
     * @param pos the candidate teleport position
     * @return {@code true} if the minion can safely teleport to {@code pos}
     */
    private boolean canTeleportTo(BlockPos pos) {
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(this, pos);
        if (pathType != PathType.WALKABLE) {
            return false;
        } else {
            BlockState blockStateBelow = this.level().getBlockState(pos.below());
            if (!this.canFlyToOwner() && blockStateBelow.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos delta = pos.subtract(this.blockPosition());
                return this.level().noCollision(this, this.getBoundingBox().move(delta));
            }
        }
    }

    /**
     * @return {@code true} if this minion cannot currently move toward its
     * owner because it is a passenger, can be leashed, or its owner
     * is a spectator
     */
    public final boolean unableToMoveToOwner() {
        return this.isPassenger() || this.mayBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
    }

    /**
     * @return {@code true} if this minion type is allowed to teleport onto
     * leaf blocks when returning to its owner; {@code false} by default
     */
    protected boolean canFlyToOwner() {
        return false;
    }


    /**
     * @param openedChestPos the position of the container this minion is
     *                       currently visually opening, or {@code null} to clear it
     */
    public void setOpenedChestPos(@Nullable BlockPos openedChestPos) {
        this.openedChestPos = openedChestPos;
    }

    /**
     * Clears the currently tracked opened-container position.
     */
    public void clearOpenedChestPos() {
        this.openedChestPos = null;
    }

    /**
     * Reports whether this minion is considered to have {@code blockPos}'s
     * container open, accounting for double chests (where either half of the
     * chest counts as the same container).
     *
     * @param container the opener counter for the container in question
     * @param blockPos  the position of the container being queried
     * @return {@code true} if this minion currently has that container open
     */
    @Override
    public boolean hasContainerOpen(@NotNull ContainerOpenersCounter container, @NotNull BlockPos blockPos) {
        if (this.openedChestPos == null) {
            return false;
        } else {
            BlockState blockState = this.level().getBlockState(this.openedChestPos);
            return this.openedChestPos.equals(blockPos)
                    || blockState.getBlock() instanceof ChestBlock
                    && blockState.getValue(ChestBlock.TYPE) != ChestType.SINGLE
                    && ChestBlock.getConnectedBlockPos(this.openedChestPos, blockState).equals(blockPos);
        }
    }

    /**
     * @return the maximum distance (in blocks) at which this minion can interact with a container
     */
    @Override
    public double getContainerInteractionRange() {
        return 3.0D;
    }

    /**
     * @return the identifiers of the bonus items currently equipped on this minion
     */
    public @NotNull List<Identifier> getBonuses() {
        return this.bonuses;
    }

    /**
     * Replaces this minion's equipped bonus list.
     *
     * @param bonuses the new list of bonus item identifiers
     */
    public void setBonuses(@NotNull List<Identifier> bonuses) {
        this.bonuses = new ArrayList<>(bonuses);
    }

    /**
     * Aggregates the huntable entity types contributed by all of this
     * minion's equipped bonuses.
     *
     * @return the combined set of entity types this minion may hunt
     */
    public @NotNull Set<EntityType<?>> getHuntableTargets() {
        Set<EntityType<?>> targets = new HashSet<>();
        for (Identifier bonusId : this.bonuses) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> targets.addAll(bonus.getHuntableTargets()));
        }
        return targets;
    }

    /**
     * @param type the bonus type to look for
     * @return {@code true} if any of this minion's equipped bonuses matches {@code type}
     */
    public boolean hasBonusType(@NotNull BonusType type) {
        for (Identifier bonusId : this.bonuses) {
            Optional<AbstractBonusItem> bonus = BonusUtil.resolve(bonusId);
            if (bonus.isPresent() && bonus.get().getBonusTypes() == type) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBonus(@NotNull Identifier bonusId) {
        return this.bonuses.contains(bonusId);
    }

    /**
     * @return {@code true} if any equipped bonus marks this minion as
     * sedentary (disabling combat/following goals in favor of
     * stationary farming behavior)
     */
    public boolean isSedentary() {
        for (Identifier bonusId : this.bonuses) {
            Optional<AbstractBonusItem> bonus = BonusUtil.resolve(bonusId);
            if (bonus.isPresent() && bonus.get().isSedentary()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@code true} if any equipped bonus grants this minion the ability to automatically plant seeds
     */
    public boolean canAutoPlant() {
        return hasBonusTrigger(BonusTrigger.AUTO_PLANT);
    }

    /**
     * @return {@code true} if any equipped bonus grants this minion the ability to automatically till soil
     */
    public boolean canAutoTill() {
        return hasBonusTrigger(BonusTrigger.AUTO_TILL);
    }

    /**
     * @param trigger the trigger to look for
     * @return {@code true} if any of this minion's equipped bonuses declares {@code trigger}
     */
    private boolean hasBonusTrigger(@NotNull BonusTrigger trigger) {
        for (Identifier bonusId : this.bonuses) {
            Optional<AbstractBonusItem> bonus = BonusUtil.resolve(bonusId);
            if (bonus.isPresent() && bonus.get().getBonusTrigger() == trigger) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calls {@link AbstractBonusItem#applyEffectes(AbstractMinion)} on every
     * equipped bonus whose {@link AbstractBonusItem#getBonusTrigger()}
     * matches {@code trigger}, regardless of minion type.
     *
     * @param trigger the trigger being fired
     */
    private void fireTrigger(@NotNull BonusTrigger trigger) {
        for (Identifier bonusId : this.bonuses) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> {
                if (bonus.getBonusTrigger() == trigger) {
                    bonus.applyEffectes(this);
                }
            });
        }
    }

    /**
     * Same as {@link #fireTrigger(BonusTrigger)}, but only for bonuses this
     * concrete minion type has registered in {@link #getSyncedBonusItem()};
     * <p>
     * each minion decides what "synced" means for it (e.g. {@link
     * net.necrocraft.world.entity.minion.impl.ParchedMinion} applies {@link
     * AbstractBonusItem#applySyncedEffect(AbstractMinion)}).
     *
     * @param trigger the trigger being fired
     */
    private void fireSyncedTrigger(@NotNull BonusTrigger trigger) {
        for (Identifier bonusId : this.bonuses) {
            boolean isSynced = false;
            for (DeferredItem<@NotNull AbstractBonusItem> synced : this.getSyncedBonusItem()) {
                if (synced.getId().equals(bonusId)) {
                    isSynced = true;
                    break;
                }
            }

            if (isSynced) {
                BonusUtil.resolve(bonusId).ifPresent(bonus -> {
                    if (bonus.getBonusTrigger() == trigger) {
                        bonus.applySyncedEffect(this);
                    }
                });
            }
        }
    }

    public ArrayList<DeferredItem<@NotNull AbstractBonusItem>> getSyncedBonusItem() {
        return SYNCED_BONUS;
    }


    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        this.lastCombatTick = this.tickCount;
        fireTrigger(BonusTrigger.ON_HIT);
        fireSyncedTrigger(BonusTrigger.ON_HIT);
        return super.hurtServer(level, source, damage);
    }

    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity target) {
        this.lastCombatTick = this.tickCount;
        fireTrigger(BonusTrigger.ON_DAMAGE);
        fireSyncedTrigger(BonusTrigger.ON_DAMAGE);
        return super.doHurtTarget(level, target);
    }

    /**
     * Fires {@link BonusTrigger#ON_KILL} whenever this minion lands the
     * killing blow on another living entity.
     *
     * @param level  the server level the kill occurred in
     * @param entity the entity that was killed
     */
    @Override
    public boolean killedEntity(@NotNull ServerLevel level, @NotNull LivingEntity entity, @NotNull DamageSource source) {
        fireTrigger(BonusTrigger.ON_KILL);
        fireSyncedTrigger(BonusTrigger.ON_KILL);

        return super.killedEntity(level, entity, source);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            fireSyncedTrigger(BonusTrigger.ON_TICK);
            fireTrigger(BonusTrigger.ON_TICK);
        }
    }

    /**
     * On death, fires {@link BonusTrigger#ON_DEATH} on any equipped bonus
     * that declares it, and drops this minion's carried inventory on the
     * ground before proceeding with vanilla death handling.
     *
     * @param damageSource the source of the killing blow
     */
    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (this.level() instanceof ServerLevel) {
            fireTrigger(BonusTrigger.ON_DEATH);
            fireSyncedTrigger(BonusTrigger.ON_DEATH);
            dropItems();
            dropNemesisShards();

            if(this.getNemesis() && damageSource.getEntity() instanceof ServerPlayer player && isSummonedBy(player)){
                AdvancementUtil.grant(player, "put_it_down");
            }
        }
        super.die(damageSource);
    }

    /**
     * Drops {@link ModItems#NEMESIS_SHARD} on death.
     * <p>
     * A minion already corrupted by Nemesis ({@link #getNemesis()}) always drops
     * shards, in a random amount between 1 and {@link #MAX_NEMESIS_SHARD_DROPS}
     * inclusive. An uncorrupted minion instead has a
     * {@link #NEMESIS_SHARD_DROP_CHANCE} chance of dropping a single shard,
     * regardless of what killed it.
     */
    private void dropNemesisShards() {
        if (this.getNemesis()) {
            int count = 1 + this.random.nextInt(MAX_NEMESIS_SHARD_DROPS);
            this.drop(new ItemStack(ModItems.NEMESIS_SHARD.get(), count), true, false);
        } else if (this.random.nextFloat() < NEMESIS_SHARD_DROP_CHANCE) {
            this.drop(new ItemStack(ModItems.NEMESIS_SHARD.get()), true, false);
        }
    }


    public void dropItems(){
        Containers.dropContents(this.level(), this, this);
    }

    /**
     * @param level  the level the minion is in
     * @param source the damage source to check
     * @return {@code true} if this minion is invulnerable to {@code source} (delegates to vanilla behavior)
     */
    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource source) {
        return super.isInvulnerableTo(level, source);
    }

    /**
     * @return {@code true} if this minion has at least one huntable target type configured, allowing it to pick up dropped loot
     */
    @Override
    public boolean canPickUpLoot() {
        return !this.getHuntableTargets().isEmpty();
    }

    /**
     * Picks up an item entity into this minion's own inventory (rather than
     * equipment slots), storing as much as fits and leaving the remainder on
     * the ground item entity.
     *
     * @param level  the server level the pickup occurs in
     * @param entity the item entity being picked up
     */
    @Override
    protected void pickUpItem(ServerLevel level, ItemEntity entity) {
        if (!this.canPickUpLoot()) {
            return;
        }

        ItemStack stack = entity.getItem();
        int originalCount = stack.getCount();

        ItemStack remainder = addToInventory(stack.copy());

        this.onItemPickup(entity);
        this.take(entity, originalCount - remainder.getCount());

        if (remainder.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(remainder);
        }
    }

    /**
     * Inserts as much of {@code stack} as possible into this minion's
     * inventory, first topping up existing matching stacks, then filling
     * empty slots.
     *
     * @param stack the stack to insert (mutated in place as items are moved)
     * @return whatever portion of {@code stack} could not be inserted (may be empty)
     */
    private ItemStack addToInventory(@NotNull ItemStack stack) {
        for (int i = 0; i < this.inventoryItems.size() && !stack.isEmpty(); i++) {
            ItemStack slot = this.inventoryItems.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                if (space > 0) {
                    int moved = Math.min(space, stack.getCount());
                    slot.grow(moved);
                    stack.shrink(moved);
                    this.setChanged();
                }
            }
        }

        for (int i = 0; i < this.inventoryItems.size() && !stack.isEmpty(); i++) {
            if (this.inventoryItems.get(i).isEmpty()) {
                int moved = Math.min(stack.getMaxStackSize(), stack.getCount());
                ItemStack toPlace = stack.copy();
                toPlace.setCount(moved);
                this.setItem(i, toPlace);
                stack.shrink(moved);
            }
        }

        return stack;
    }

    /**
     * Stores as much of the given item stack as possible in this minion's
     * inventory.
     *
     * @param stack the stack to store
     * @return whatever portion could not be stored (may be empty)
     */
    public @NotNull ItemStack storeItemStack(@NotNull ItemStack stack) {
        return addToInventory(stack);
    }

    /**
     * @param item the item to look for
     * @return {@code true} if this minion's inventory contains at least one of {@code item}
     */
    public boolean hasItem(@NotNull Item item) {
        for (ItemStack stack : this.inventoryItems) {
            if (!stack.isEmpty() && stack.is(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes up to {@code amount} of {@code item} from this minion's
     * inventory, across as many slots as needed.
     *
     * @param item   the item to consume
     * @param amount the desired quantity to remove
     * @return {@code true} if the full {@code amount} was successfully removed
     */
    public boolean consumeItem(@NotNull Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < this.inventoryItems.size() && remaining > 0; i++) {
            ItemStack slot = this.inventoryItems.get(i);
            if (!slot.isEmpty() && slot.is(item)) {
                int take = Math.min(remaining, slot.getCount());
                slot.shrink(take);
                remaining -= take;
                this.setChanged();
            }
        }
        return remaining == 0;
    }

    /**
     * @return the fixed number of slots in this minion's inventory ({@value #INVENTORY_SIZE})
     */
    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    /**
     * @return {@code true} if every slot in this minion's inventory is empty
     */
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventoryItems) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param slot the inventory slot index
     * @return the item stack in that slot
     */
    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventoryItems.get(slot);
    }

    /**
     * Removes up to {@code amount} items from {@code slot}.
     *
     * @param slot   the inventory slot index
     * @param amount the maximum number of items to remove
     * @return the removed item stack (may be smaller than {@code amount} or empty)
     */
    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.inventoryItems, slot, amount);
        if (!result.isEmpty()) {
            this.setChanged();
        }
        return result;
    }

    /**
     * Removes the entire stack from {@code slot} without triggering a
     * container-changed update.
     *
     * @param slot the inventory slot index
     * @return the removed item stack
     */
    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventoryItems, slot);
    }

    /**
     * Places {@code stack} into {@code slot}, clamping its count to this
     * container's max stack size.
     *
     * @param slot  the inventory slot index
     * @param stack the item stack to place
     */
    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventoryItems.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    /**
     * No-op: this container does not need to notify external listeners of changes.
     */
    @Override
    public void setChanged() {
    }

    /**
     * @param player the player checking validity
     * @return {@code true} if this minion is alive and the player is within 8 blocks (64 squared) of it
     */
    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    /**
     * Empties this minion's inventory of all items.
     */
    @Override
    public void clearContent() {
        this.inventoryItems.clear();
    }

    /**
     * Opens this minion's inventory menu for the interacting player when
     * right-clicked with an empty main hand interaction (and not sneaking).
     * <p>
     * A minion corrupted by a {@link net.necrocraft.world.item.equipment.NemesisShard}
     * ({@link #getNemesis()}) has gone feral: it no longer recognizes its owner
     * for this purpose, and its inventory cannot be opened at all.
     *
     * @param player the interacting player
     * @param hand   the hand used to interact
     * @return {@link InteractionResult#SUCCESS} if the menu was opened, otherwise the vanilla result
     */
    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        boolean holdingRevocationScepter = player.getItemInHand(hand).is(ModItems.REVOCATION_SCEPTER);
        boolean holdingNemesisShard = player.getItemInHand(hand).is(ModItems.NEMESIS_SHARD);

        if (hand == InteractionHand.MAIN_HAND
                && !player.isSecondaryUseActive()
                && !holdingRevocationScepter
                && !holdingNemesisShard
                && !this.getNemesis()
                && Objects.requireNonNull(this.getOwnerReference()).matches(player)) {
            if (!this.level().isClientSide()) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInv, _) -> new MinionInventoryMenu(containerId, playerInv, this),
                        this.getDisplayName()
                ), buf -> buf.writeVarInt(this.getId()));
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /**
     * The minion can resist to the fire with some bonus
     *
     * @return true if the minion have some bonus
     */
    @Override
    public boolean fireImmune() {
        return bonuses.contains(ModItems.NETHERIFIED_BONE_BONUS_ITEM.getId());
    }

    public boolean getNemesis(){
        return this.entityData.get(DATA_NEMESIS_ID);
    }

    public void setNemesis(boolean newNemesis){
        this.entityData.set(DATA_NEMESIS_ID, newNemesis);
    }
}