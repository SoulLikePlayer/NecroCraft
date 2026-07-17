package net.necrocraft.world.item.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EquipmentSlot;
import net.necrocraft.world.entity.minion.AbstractMinion;
import net.necrocraft.world.entity.minion.registry.MinionRegistry;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.bonus.BonusUtil;
import net.necrocraft.world.item.component.SoulData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SoulTotem extends Item {
    private static final EquipmentSlot[] EQUIPMENT_ORDER = new EquipmentSlot[] {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };

    public SoulTotem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SoulData soulData = stack.get(ModDataComponents.SOUL_DATA.get());

        if (soulData == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Optional<Holder.Reference<@NotNull EntityType<?>>> capturedType = BuiltInRegistries.ENTITY_TYPE.get(soulData.entityType());
        EntityType<? extends @NotNull AbstractMinion> minionType = MinionRegistry.getMinionFor(capturedType);
        if (minionType == null) {
            return InteractionResult.FAIL;
        }

        AbstractMinion minion = minionType.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (minion == null) {
            return InteractionResult.FAIL;
        }

        minion.setOwner(player);
        minion.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        equipMinion(minion, soulData);
        applyBonuses(minion, soulData);
        ((ServerLevel) level).addFreshEntity(minion);

        return InteractionResult.SUCCESS;
    }

    private static void equipMinion(AbstractMinion minion, SoulData soulData) {
        List<ItemStack> equipment = soulData.equipment();
        for (int i = 0; i < EQUIPMENT_ORDER.length && i < equipment.size(); i++) {
            ItemStack piece = equipment.get(i);
            if (!piece.isEmpty()) {
                minion.setItemSlot(EQUIPMENT_ORDER[i], piece.copy());
            }
        }
    }

    private static void applyBonuses(AbstractMinion minion, SoulData soulData) {
        for (Identifier bonusId : soulData.bonuses()) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> bonus.applyEffectes(minion));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack,
                                @NotNull TooltipContext context,
                                @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> builder,
                                @NotNull TooltipFlag tooltipFlag) {

        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.empty());

        SoulData soulData = itemStack.get(ModDataComponents.SOUL_DATA.get());
        if (soulData == null) {
            builder.accept(Component.translatable("item.necrocraft.soul_totem.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        Optional<Holder.Reference<@NotNull EntityType<?>>> capturedType =
                BuiltInRegistries.ENTITY_TYPE.get(soulData.entityType());

        if (capturedType.isEmpty()) {
            builder.accept(Component.translatable("item.necrocraft.soul_totem.unknown")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        EntityType<?> entityType = capturedType.get().value();
        builder.accept(Component.translatable("item.necrocraft.soul_totem.summons",
                        entityType.getDescription())
                .withStyle(ChatFormatting.GOLD));

        List<ItemStack> equipment = soulData.equipment();
        boolean hasEquipment = equipment.stream().anyMatch(piece -> !piece.isEmpty());
        if (hasEquipment) {
            builder.accept(Component.empty());
            builder.accept(Component.translatable("item.necrocraft.soul_totem.equipment")
                    .withStyle(ChatFormatting.GRAY));
            for (ItemStack piece : equipment) {
                if (!piece.isEmpty()) {
                    builder.accept(Component.literal(" - ")
                            .append(piece.getHoverName())
                            .withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        }

        List<Identifier> bonuses = soulData.bonuses();
        if (!bonuses.isEmpty()) {
            builder.accept(Component.empty());
            builder.accept(Component.translatable("item.necrocraft.soul_totem.bonuses")
                    .withStyle(ChatFormatting.GRAY));
            for (Identifier bonusId : bonuses) {
                BonusUtil.resolve(bonusId).ifPresent(bonus ->
                        builder.accept(Component.literal(" - ")
                                .append(new ItemStack(bonus).getHoverName())
                                .withStyle(ChatFormatting.LIGHT_PURPLE)));
            }
        }
    }
}