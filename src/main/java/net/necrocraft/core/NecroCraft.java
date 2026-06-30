package net.necrocraft.core;

import com.mojang.logging.LogUtils;
import net.necrocraft.command.NecroCraftCommands;
import net.necrocraft.world.entity.ModEntity;
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

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        NecroCraftCommands.register(event.getDispatcher());
    }
}
