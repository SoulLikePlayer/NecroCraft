package net.necrocraft.world.item.bonus.impl;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.item.bonus.AbstractBonusItem;
import net.necrocraft.world.item.bonus.BonusTrigger;
import net.necrocraft.world.item.bonus.BonusType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TrialVaultBonusItem extends AbstractBonusItem {

    private static final ResourceKey<@NotNull LootTable> TABLE_COMMON =
            ResourceKey.create(Registries.LOOT_TABLE,
                    Identifier.withDefaultNamespace("chests/trial_chambers/reward"));

    private static final ResourceKey<@NotNull LootTable> TABLE_JACKPOT =
            ResourceKey.create(Registries.LOOT_TABLE,
                    Identifier.withDefaultNamespace("chests/trial_chambers/reward_ominous"));

    private enum Tier {
        NOTHING(200, 0, false),
        COMMON(60, 2, false),
        RARE(15, 3, false),
        EPIC(4, 4, false),
        LEGENDARY(1, 5, true);

        final int weight;
        final int itemCount;
        final boolean ominous;

        Tier(int weight, int itemCount, boolean ominous) {
            this.weight = weight;
            this.itemCount = itemCount;
            this.ominous = ominous;
        }

        static Tier roll(RandomSource random) {
            int total = 0;
            for (Tier t : values()) total += t.weight;
            int r = random.nextInt(total);
            int acc = 0;
            for (Tier t : values()) {
                acc += t.weight;
                if (r < acc) return t;
            }
            return NOTHING;
        }
    }

    public TrialVaultBonusItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BonusType getBonusTypes() {
        return BonusType.PASSIVE;
    }

    @Override
    public @NotNull BonusTrigger getBonusTrigger() {
        return BonusTrigger.ON_KILL;
    }

    @Override
    public void applyEffectes(@NotNull AbstractMinion minion) {
        Level level = minion.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        RandomSource random = serverLevel.getRandom();
        Tier tier = Tier.roll(random);

        NecroCraft.LOGGER.info(String.valueOf(tier));

        if (tier == Tier.NOTHING) {
            playRejected(serverLevel, minion);
            return;
        }

        ResourceKey<@NotNull LootTable> table = tier.ominous ? TABLE_JACKPOT : TABLE_COMMON;
        openVault(serverLevel, minion, tier.ominous);
        giveLoot(minion, serverLevel, table, tier.itemCount, tier.ominous);
        closeVault(serverLevel, minion, tier.ominous);
    }

    private static final int EJECT_DELAY_TICKS = 10;

    private void giveLoot(AbstractMinion minion, ServerLevel level, ResourceKey<@NotNull LootTable> tableKey, int itemCount, boolean ominous) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, minion.position())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, minion)
                .create(LootContextParamSets.CHEST);

        List<ItemStack> toGive = new java.util.ArrayList<>();
        int safety = 0;
        while (toGive.size() < itemCount && safety < itemCount * 4) {
            safety++;
            for (ItemStack stack : table.getRandomItems(params)) {
                if (toGive.size() >= itemCount) break;
                toGive.add(stack);
            }
        }

        scheduleEjection(minion, level, toGive, 0, ominous);
    }

    private void scheduleEjection(AbstractMinion minion, ServerLevel level, List<ItemStack> toGive, int index, boolean ominous) {
        if (index >= toGive.size()) return;
        if (minion.isRemoved() || !minion.isAlive()) return;

        ItemStack stack = toGive.get(index);
        minion.spawnAtLocation(level, stack);
        playEjectItem(level, minion, ominous);

        if (index + 1 < toGive.size()) {
            int targetTick = level.getServer().getTickCount() + EJECT_DELAY_TICKS;
            level.getServer().execute(new TickTask(targetTick,
                    () -> scheduleEjection(minion, level, toGive, index + 1, ominous)));
        }
    }

    private void openVault(ServerLevel level, AbstractMinion minion, boolean ominous) {
        level.playSound(null, minion.blockPosition(),
                ominous ? SoundEvents.VAULT_ACTIVATE : SoundEvents.VAULT_OPEN_SHUTTER,
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void playEjectItem(ServerLevel level, AbstractMinion minion, boolean ominous) {
        level.playSound(null, minion.blockPosition(),
                SoundEvents.VAULT_EJECT_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (ominous) {
            level.sendParticles(ParticleTypes.OMINOUS_SPAWNING,
                    minion.getX(), minion.getY() + 1, minion.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
        } else {
            level.sendParticles(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER,
                    minion.getX(), minion.getY() + 1, minion.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
        }
    }

    private void closeVault(ServerLevel level, AbstractMinion minion, boolean ominous) {
        level.playSound(null, minion.blockPosition(),
                ominous ? SoundEvents.VAULT_DEACTIVATE : SoundEvents.VAULT_CLOSE_SHUTTER,
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private void playRejected(ServerLevel level, AbstractMinion minion) {
        level.playSound(null, minion.blockPosition(),
                SoundEvents.VAULT_REJECT_REWARDED_PLAYER, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.sendParticles(ParticleTypes.SMOKE,
                minion.getX(), minion.getY() + 1, minion.getZ(), 6, 0.2, 0.2, 0.2, 0.02);
    }

}