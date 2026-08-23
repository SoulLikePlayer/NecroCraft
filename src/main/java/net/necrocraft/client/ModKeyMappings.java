package net.necrocraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.necrocraft.core.NecroCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * The two keybinds available while the player carries Soul of Undead: one to
 * self-heal, one to empower nearby minions. Registered under their own
 * NecroCraft {@link KeyMapping.Category} so they show up together and
 * labelled in the Controls screen (translation key
 * {@code key.category.necrocraft.necrocraft}, see {@link KeyMapping.Category#label()}).
 */
@EventBusSubscriber(modid = NecroCraft.MODID, value = Dist.CLIENT)
public final class ModKeyMappings {

    public static final KeyMapping.Category CATEGORY_NECROCRAFT =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NecroCraft.MODID, "necrocraft"));

    public static final KeyMapping SOUL_HEAL = new KeyMapping(
            "key.necrocraft.soul_heal",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_H,
            CATEGORY_NECROCRAFT
    );

    public static final KeyMapping SOUL_EMPOWER = new KeyMapping(
            "key.necrocraft.soul_empower",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_J,
            CATEGORY_NECROCRAFT
    );

    private ModKeyMappings() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SOUL_HEAL);
        event.register(SOUL_EMPOWER);
    }
}