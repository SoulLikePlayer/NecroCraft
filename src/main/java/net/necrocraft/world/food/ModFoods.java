package net.necrocraft.world.food;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties NECROTIC_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(1.2F).alwaysEdible().build();
    public static final FoodProperties PUTRID_VENOM_VIAL = (new FoodProperties.Builder().nutrition(-3).saturationModifier(0f).alwaysEdible().build());
    public static final FoodProperties DEHYDRATED_ROTTEN_FLESH = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.05F).build();
}
