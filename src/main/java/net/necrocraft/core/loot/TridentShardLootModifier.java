package net.necrocraft.core.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.necrocraft.world.item.ModItems;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class TridentShardLootModifier extends LootModifier {

    public static final MapCodec<TridentShardLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .apply(inst, TridentShardLootModifier::new));

    private static final float CHANCE = 0.60f;

    public TridentShardLootModifier(LootItemCondition[] conditions, int priority) {
        super(conditions, priority);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < CHANCE) {
            generatedLoot.add(new ItemStack(ModItems.TRIDENT_SHARD_BONUS_ITEM.get()));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}