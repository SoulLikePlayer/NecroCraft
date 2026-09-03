package net.necrocraft.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.necrocraft.core.NecroCraft;
import net.necrocraft.world.item.ModDataComponents;
import net.necrocraft.world.item.component.ObedienceActionData;
import net.necrocraft.world.item.equipment.NemesisShard;
import net.necrocraft.world.item.equipment.ObedienceScepter;
import net.necrocraft.world.item.equipment.bone.classic.BoneEquipment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;

@EventBusSubscriber(modid = NecroCraft.MODID, value = Dist.CLIENT)
public class TooltipHandlerModEvent {
    @SubscribeEvent
    public static void onAddAttributeTooltips(AddAttributeTooltipsEvent event) {
        if(event.shouldShow()) {
            Item item = event.getStack().getItem();
            if (item instanceof BoneEquipment) {
                event.addTooltipLines(Component.translatable("item.necrocraft.bone_equipement.gain_soul")
                        .withStyle(ChatFormatting.DARK_GREEN)
                );
            }

            if (item instanceof NemesisShard) {
                event.addTooltipLines(Component.empty());
                event.addTooltipLines(Component.translatable("item.necrocraft.nemesis_shard.desc")
                        .withStyle(ChatFormatting.DARK_RED));
            }

            if (item instanceof ObedienceScepter) {
                event.addTooltipLines(Component.empty());
                event.addTooltipLines(Component.translatable("item.necrocraft.obedience_scepter.desc")
                        .withStyle(ChatFormatting.GRAY));

                ObedienceScepter.ObedienceAction currentAction = event.getStack()
                        .getOrDefault(ModDataComponents.OBEDIENCE_ACTION_DATA.get(),
                                new ObedienceActionData(ObedienceScepter.ObedienceAction.WANDERING))
                        .action();

                event.addTooltipLines(Component.translatable(
                                        "item.necrocraft.obedience_scepter.current_action",
                                        Component.translatable("item.necrocraft.obedience_scepter.action."
                                                + currentAction.getSerializedName())
                                )
                                .withStyle(ChatFormatting.GRAY)
                );
            }
        }
    }
}