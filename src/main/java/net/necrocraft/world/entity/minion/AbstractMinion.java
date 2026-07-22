package net.necrocraft.world.entity.minion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.necrocraft.world.entity.ai.goal.*;
import net.necrocraft.world.inventory.MinionInventoryMenu;
import net.necrocraft.world.item.bonus.BonusUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Abstract class who define what is a minion in NecroCraft.
 * A minion is a summonable mob define by a summoner
 */
public class AbstractMinion extends PathfinderMob implements OwnableEntity, Container {
    public static final int TELEPORT_WHEN_DISTANCE_IS_SQ = 144;

    private static final int MIN_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 2;
    private static final int MAX_HORIZONTAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 3;
    private static final int MAX_VERTICAL_DISTANCE_FROM_TARGET_AFTER_TELEPORTING = 1;

    private static final int INVENTORY_SIZE = 27;

    protected static final EntityDataAccessor<@NotNull Optional<EntityReference<@NotNull LivingEntity>>> DATA_SUMMONER_UUID_ID =
            SynchedEntityData.defineId(AbstractMinion.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    private List<Identifier> bonuses = new ArrayList<>();

    private final NonNullList<@NotNull ItemStack> inventoryItems = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    protected AbstractMinion(EntityType<? extends @NotNull PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowSummonerGoal(this, 1.0F, 10.0F, 2.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new SummonerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SummonerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HuntPreyGoal(this, 16.0F));
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
        EntityReference<@NotNull LivingEntity> summoner = this.getOwnerReference();
        EntityReference.store(summoner, output, "summoner");
        output.store("bonuses", Identifier.CODEC.listOf(), this.bonuses);
        ContainerHelper.saveAllItems(output, this.inventoryItems);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<@NotNull LivingEntity> owner = EntityReference.readWithOldOwnerConversion(input, "summoner", this.level());
        if (owner != null) {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.of(owner));
        }
        this.bonuses = new ArrayList<>(input.read("bonuses", Identifier.CODEC.listOf()).orElse(List.of()));
        this.inventoryItems.clear();
        ContainerHelper.loadAllItems(input, this.inventoryItems);
    }

    public EntityReference<@NotNull LivingEntity> getOwnerReference() {
        return this.entityData
                .get(DATA_SUMMONER_UUID_ID)
                .orElse(null);
    }

    public boolean isSummonedBy(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public void tryToTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            this.teleportToAroundBlockPos(owner.blockPosition());
        }

    }

    public boolean shouldTryTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        return owner != null && this.distanceToSqr(this.getOwner()) >= (double)144.0F;
    }

    private void teleportToAroundBlockPos(BlockPos targetPos) {
        for(int attempt = 0; attempt < 10; ++attempt) {
            int xd = this.random.nextIntBetweenInclusive(-3, 3);
            int zd = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(xd) >= 2 || Math.abs(zd) >= 2) {
                int yd = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(targetPos.getX() + xd, targetPos.getY() + yd, targetPos.getZ() + zd)) {
                    return;
                }
            }
        }

    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.snapTo((double)x + (double)0.5F, (double)y, (double)z + (double)0.5F, this.getYRot(), this.getXRot());
            this.navigation.stop();
            return true;
        }
    }

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

    public final boolean unableToMoveToOwner() {
        return this.isPassenger() || this.mayBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
    }

    protected boolean canFlyToOwner() {
        return false;
    }

    public void setOwner(LivingEntity owner) {
        if (owner != null) {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.of(EntityReference.of(owner)));
        } else {
            this.entityData.set(DATA_SUMMONER_UUID_ID, Optional.empty());
        }
    }

    public void setBonuses(@NotNull List<Identifier> bonuses) {
        this.bonuses = new ArrayList<>(bonuses);
    }

    public @NotNull List<Identifier> getBonuses() {
        return this.bonuses;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource source) {
        return super.isInvulnerableTo(level, source);
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.getHuntableTargets().isEmpty();
    }

    @Override
    protected void pickUpItem(ServerLevel level, ItemEntity entity) {
        if(!this.canPickUpLoot()){
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

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, openingPlayer) -> new MinionInventoryMenu(containerId, playerInventory, this),
                        this.getDisplayName()
                ));
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        if (this.level() instanceof ServerLevel serverLevel) {
            triggerPostMortemBonuses(serverLevel);
            Containers.dropContents(this.level(), this, this);
        }
        super.die(damageSource);
    }

    private void triggerPostMortemBonuses(ServerLevel serverLevel) {
        for (Identifier bonusId : this.bonuses) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> bonus.onDeath(this, serverLevel));
        }
    }


    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventoryItems) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventoryItems.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.inventoryItems, slot, amount);
        if (!result.isEmpty()) {
            this.setChanged();
        }
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventoryItems, slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventoryItems.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.inventoryItems.clear();
    }

    public @NotNull Set<EntityType<?>> getHuntableTargets() {
        Set<EntityType<?>> targets = new HashSet<>();
        for (Identifier bonusId : this.bonuses) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> targets.addAll(bonus.getHuntableTargets()));
        }
        return targets;
    }
}