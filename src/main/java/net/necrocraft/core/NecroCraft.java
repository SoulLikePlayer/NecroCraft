package net.necrocraft.core;

import com.mojang.logging.LogUtils;
import net.necrocraft.client.ModKeyMappings;
import net.necrocraft.command.NecroCraftCommands;
import net.necrocraft.core.loot.ModLootModifier;
import net.necrocraft.world.effect.ModMobEffects;
import net.necrocraft.world.entity.ModEntity;
import net.necrocraft.world.inventory.ModMenuTypes;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.ModItemGroups;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.alchemy.ModPotions;
import net.necrocraft.world.level.block.ModBlock;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(NecroCraft.MODID)
public class NecroCraft {
    public static final String MODID = "necrocraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NecroCraft(IEventBus modEventBus, ModContainer modContainer) {
        ModEntity.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModItemGroups.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModMobEffects.MOB_EFFECTS.register(modEventBus);
        ModLootModifier.LOOT_MODIFIERS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        NecroCraftCommands.register(event.getDispatcher());
    }
}
