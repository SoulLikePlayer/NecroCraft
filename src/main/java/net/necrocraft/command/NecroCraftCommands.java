package net.necrocraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.entity.minion.AbstractMinion;

/**
 * Registers and handles the {@code /summon-minion} command, which lets a
 * game master spawn a tamed {@link AbstractMinion} owned by the executing player.
 */
public class NecroCraftCommands {

    /**
     * Autocomplete suggestions offered for the {@code type} argument of
     * {@code /summon-minion} (currently {@code "zombie"} and {@code "skeleton"}).
     */
    private static final SuggestionProvider<CommandSourceStack> MINION_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"zombie", "skeleton", "bogged", "stray", "drowned", "husk", "parched"}, builder);

    /**
     * Registers the {@code /summon-minion} command against the given dispatcher.
     * <p>
     * The command requires {@link PermissionLevel#GAMEMASTERS} and takes a single
     * mandatory {@code type} argument identifying which minion to summon.
     *
     * @param dispatcher the Brigadier command dispatcher to register the command with
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("summon-minion")
                        .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(MINION_SUGGESTIONS)
                                .executes(NecroCraftCommands::summonMinion)
                        )
        );

        dispatcher.register(
                Commands.literal("soul-gauge")
                        .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.literal("add")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                                .executes(ctx -> adjustSoulGauge(ctx, SoulGaugeOperation.ADD))
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                                .executes(ctx -> adjustSoulGauge(ctx, SoulGaugeOperation.REMOVE))
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                                                .executes(ctx -> adjustSoulGauge(ctx, SoulGaugeOperation.SET))
                                        )
                                )
                        )
        );
    }

    /**
     * The three ways {@code /soul-gauge} can update a player's soul gauge value.
     */
    private enum SoulGaugeOperation {
        ADD,
        REMOVE,
        SET
    }

    /**
     * Executes the {@code /summon-minion} command: spawns the requested minion
     * type at the executing player's position and assigns that player as its owner.
     * <p>
     * Fails (with a translated error message sent back to the source) if the
     * command was not run by a player, or if {@code type} does not match a
     * known minion type.
     *
     * @param ctx the Brigadier command context, providing the source and the {@code type} argument
     * @return {@code 1} on success, {@code 0} if the command failed (wrong source or unknown type)
     */
    private static int summonMinion(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String minionType = StringArgumentType.getString(ctx, "type");

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.necrocraft.summon_minion.not_player"));
            return 0;
        }

        ServerLevel level = source.getLevel();

        AbstractMinion minion = switch (minionType.toLowerCase()) {
            case "zombie" -> ModEntity.ZOMBIE_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "skeleton" -> ModEntity.SKELETON_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "bogged" -> ModEntity.BOGGED_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "drowned" -> ModEntity.DROWNED_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "husk" -> ModEntity.HUSK_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "parched" -> ModEntity.PARCHED_MINION.get().create(level, EntitySpawnReason.COMMAND);
            case "stray" -> ModEntity.STRAY_MINION.get().create(level, EntitySpawnReason.COMMAND);
            default -> null;
        };

        if (minion == null) {
            source.sendFailure(Component.translatable("command.necrocraft.summon_minion.unknown_type", minionType));
            return 0;
        }

        minion.setOwner(player);
        minion.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        level.addFreshEntity(minion);

        source.sendSuccess(
                () -> Component.translatable("command.necrocraft.summon_minion.success", minionType),
                false
        );

        return 1;
    }

    /**
     * Executes the {@code /soul-gauge <add|remove|set> <target> <value>} command:
     * updates the {@code target} player's {@link ModAttachments#SOUL_GAUGE} attachment
     * according to {@code operation}, then clamps the result to the {@code [0, 100]} range.
     * <p>
     * The {@code value} argument itself is already constrained to {@code [0, 100]} by its
     * Brigadier argument type, but the clamp is still applied after add/remove so the
     * resulting gauge value can never leave that range regardless of the current value.
     *
     * @param ctx       the Brigadier command context, providing the source, {@code target} and {@code value} arguments
     * @param operation whether to add, remove, or set the gauge value
     * @return {@code 1} on success
     */
    private static int adjustSoulGauge(CommandContext<CommandSourceStack> ctx, SoulGaugeOperation operation) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        float value = FloatArgumentType.getFloat(ctx, "value");

        float current = target.getData(ModAttachments.SOUL_GAUGE);
        float updated = switch (operation) {
            case ADD -> current + value;
            case REMOVE -> current - value;
            case SET -> value;
        };
        updated = Mth.clamp(updated, 0f, 100f);

        target.setData(ModAttachments.SOUL_GAUGE, updated);

        float finalUpdated = updated;
        source.sendSuccess(
                () -> Component.translatable("command.necrocraft.soul_gauge.success", target.getName(), finalUpdated),
                false
        );

        return 1;
    }
}