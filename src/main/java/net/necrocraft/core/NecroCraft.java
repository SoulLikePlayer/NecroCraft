package net.necrocraft.core;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(NecroCraft.MODID)
public class NecroCraft {
    public static final String MODID = "necrocraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NecroCraft(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
