package net.necrocraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.entity.minion.AbstractMinion;

public class NecroCraftCommands {

    private static final SuggestionProvider<CommandSourceStack> MINION_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"zombie"}, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("summon-minion")
                        .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(MINION_SUGGESTIONS)
                                .executes(NecroCraftCommands::summonMinion)
                        )
        );
    }

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
}