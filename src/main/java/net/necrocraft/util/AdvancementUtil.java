package net.necrocraft.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.necrocraft.core.NecroCraft;

public class AdvancementUtil {

    public static void grant(ServerPlayer player, String advancementId) {
        AdvancementHolder advancementHolder = player.level().getServer()
                .getAdvancements()
                .get(Identifier.fromNamespaceAndPath(NecroCraft.MODID, NecroCraft.MODID.toLowerCase()+"/"+advancementId));

        if (advancementHolder == null) {
            return;
        }

        PlayerAdvancements playerAdvancements = player.getAdvancements();
        AdvancementProgress progress = playerAdvancements.getOrStartProgress(advancementHolder);

        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                playerAdvancements.award(advancementHolder, criterion);
            }
        }
    }
}