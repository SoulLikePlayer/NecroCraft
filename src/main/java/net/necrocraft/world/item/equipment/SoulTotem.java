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

/**
 * The item half of the soul-capture mechanic (see {@code ModEvent#onMobDeath}).
 * <p>
 * An empty soul totem does nothing when used; once stamped with
 * {@link SoulData} (captured entity type, its equipment, and its bonuses),
 * using it spends experience points and summons a tamed {@link AbstractMinion}
 * of the captured type, owned by the user and re-equipped with the stored
 * gear and bonuses. The item's tooltip also reflects the stored soul data.
 */
public class SoulTotem extends Item {

    /** Equipment slots in the order they are stored in / read from {@link SoulData#equipment()}. */
    private static final EquipmentSlot[] EQUIPMENT_ORDER = new EquipmentSlot[] {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };

    /** Flat experience-point cost charged for summoning, regardless of bonuses. */
    private static final int BASE_XP_COST = 5;
    /** Additional experience-point cost charged per bonus stored on the totem. */
    private static final int XP_COST_PER_BONUS = 3;

    /** @param properties the vanilla item properties for this item */
    public SoulTotem(Properties properties) {
        super(properties);
    }

    /**
     * Handles using the soul totem: if it carries no {@link SoulData}, the
     * interaction is passed through. Otherwise, on the server, this charges
     * the player the appropriate experience cost, resolves and spawns the
     * corresponding minion type, positions it at the player, equips it with
     * the stored gear, applies its stored bonuses, and adds it to the world.
     * <p>
     * Experience is refunded and the interaction fails if the captured entity
     * type has no registered minion mapping, or if the minion entity fails
     * to spawn.
     *
     * @param level  the level the item is used in
     * @param player the player using the item
     * @param hand   the hand the item is held in
     * @return {@link InteractionResult#PASS} if the totem carries no soul data,
     *         {@link InteractionResult#SUCCESS} on successful summon (or immediately
     *         on the client side), {@link InteractionResult#FAIL} if the player
     *         lacks the experience or the minion could not be created
     */
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

        int xpCost = calculateXpCost(soulData);
        if (!consumeExperience(player, xpCost)) {
            return InteractionResult.FAIL;
        }

        Optional<Holder.Reference<@NotNull EntityType<?>>> capturedType = BuiltInRegistries.ENTITY_TYPE.get(soulData.entityType());
        EntityType<? extends @NotNull AbstractMinion> minionType = MinionRegistry.getMinionFor(capturedType);
        if (minionType == null) {
            refundExperience(player, xpCost);
            return InteractionResult.FAIL;
        }

        AbstractMinion minion = minionType.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (minion == null) {
            refundExperience(player, xpCost);
            return InteractionResult.FAIL;
        }

        minion.setOwner(player);
        minion.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        equipMinion(minion, soulData);
        applyBonuses(minion, soulData);
        ((ServerLevel) level).addFreshEntity(minion);

        return InteractionResult.SUCCESS;
    }

    /**
     * Copies the equipment stored in {@code soulData} onto the freshly
     * summoned minion, in {@link #EQUIPMENT_ORDER} slot order.
     *
     * @param minion   the minion to equip
     * @param soulData the soul data holding the stored equipment
     */
    private static void equipMinion(AbstractMinion minion, SoulData soulData) {
        List<ItemStack> equipment = soulData.equipment();
        for (int i = 0; i < EQUIPMENT_ORDER.length && i < equipment.size(); i++) {
            ItemStack piece = equipment.get(i);
            if (!piece.isEmpty()) {
                minion.setItemSlot(EQUIPMENT_ORDER[i], piece.copy());
            }
        }
    }

    /**
     * Computes the experience-point cost of summoning from this soul data:
     * a base cost plus a per-bonus surcharge.
     *
     * @param soulData the soul data to price
     * @return the total experience-point cost
     */
    private static int calculateXpCost(SoulData soulData) {
        int bonusCount = soulData.bonuses().size();
        return BASE_XP_COST + (XP_COST_PER_BONUS * bonusCount);
    }

    /**
     * Attempts to deduct {@code cost} experience points from the player.
     * Players in creative/instabuild mode are charged nothing but still succeed.
     *
     * @param player the player to charge
     * @param cost   the experience-point cost to deduct
     * @return {@code true} if the player had enough experience (or is exempt) and was charged
     */
    private static boolean consumeExperience(Player player, int cost) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        if (player.totalExperience < cost) {
            return false;
        }
        player.giveExperiencePoints(-cost);
        return true;
    }

    /**
     * Refunds {@code cost} experience points to the player after a failed
     * summon attempt, unless they are exempt from the charge.
     *
     * @param player the player to refund
     * @param cost   the experience-point amount to give back
     */
    private static void refundExperience(Player player, int cost) {
        if (!player.getAbilities().instabuild) {
            player.giveExperiencePoints(cost);
        }
    }

    /**
     * Assigns the stored bonus list to the minion and applies each
     * resolvable bonus's summon-time effects to it.
     *
     * @param minion   the minion to apply bonuses to
     * @param soulData the soul data holding the stored bonus identifiers
     */
    private static void applyBonuses(AbstractMinion minion, SoulData soulData) {
        minion.setBonuses(soulData.bonuses());
        for (Identifier bonusId : soulData.bonuses()) {
            BonusUtil.resolve(bonusId).ifPresent(bonus -> bonus.applyEffectes(minion));
        }
        minion.setHealth(minion.getMaxHealth());
    }

    /**
     * Builds the totem's tooltip: an empty totem shows a placeholder line;
     * a stamped totem shows the mob it will summon, followed by an optional
     * list of stored equipment pieces and an optional list of stored bonuses.
     * Falls back to an "unknown" warning if the stored entity type can no
     * longer be resolved from the registry.
     *
     * @param itemStack    the totem item stack being inspected
     * @param context      the tooltip context
     * @param display      the tooltip display settings
     * @param builder      consumer used to append tooltip lines
     * @param tooltipFlag  advanced-tooltip flag (shift/F3+H state)
     */
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
                .withStyle(ChatFormatting.DARK_GRAY));

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
                                .withStyle(ChatFormatting.DARK_GRAY)));
            }
        }
    }
}