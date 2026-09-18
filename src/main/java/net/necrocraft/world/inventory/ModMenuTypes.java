package net.necrocraft.world.inventory;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.necrocraft.core.NecroCraft;
import org.jetbrains.annotations.NotNull;

public class ModMenuTypes {
    public static final DeferredRegister<@NotNull MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, NecroCraft.MODID);

    public static final DeferredHolder<@NotNull MenuType<?>, @NotNull MenuType<CarvingMenu>> CARVING_MENU =
            MENU_TYPES.register("soul_carving_table",
                    () -> IMenuTypeExtension.create((containerId, inventory, buffer) ->
                            new CarvingMenu(containerId, inventory)));

    public static final DeferredHolder<@NotNull MenuType<?>, @NotNull MenuType<@NotNull MinionInventoryMenu>> MINION_INVENTORY =
            MENU_TYPES.register("minion_inventory",
                    () -> IMenuTypeExtension.create(MinionInventoryMenu::new));
}