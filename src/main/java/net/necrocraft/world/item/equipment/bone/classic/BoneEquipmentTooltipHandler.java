package net.necrocraft.world.item.equipment.bone.classic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.necrocraft.core.NecroCraft;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;

@EventBusSubscriber(modid = NecroCraft.MODID, value = Dist.CLIENT)
public class BoneEquipmentTooltipHandler {

    @SubscribeEvent
    public static void onAddAttributeTooltips(AddAttributeTooltipsEvent event) {
        if (event.getStack().getItem() instanceof BoneEquipment && event.shouldShow()) {
            event.addTooltipLines(
                    Component.translatable("item.necrocraft.bone_equipement.gain_soul")
                            .withStyle(ChatFormatting.DARK_GREEN)
            );
        }
    }
}