package net.necrocraft.event;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.ModItems;
import net.necrocraft.world.item.alchemy.ModPotions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = NecroCraft.MODID)
public class ModRegisterEvent {

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event){
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ModItems.NECROTIC_POWDER.asItem(), ModPotions.POTION_OF_UNDEAD);
    }
}
