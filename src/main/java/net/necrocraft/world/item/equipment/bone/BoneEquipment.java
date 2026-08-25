package net.necrocraft.world.item.equipment.bone;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.necrocraft.core.ModAttachments;
import net.necrocraft.world.effect.ModMobEffects;
import org.jetbrains.annotations.NotNull;

public class BoneEquipment extends Item {

    public BoneEquipment(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack itemStack, @NotNull LivingEntity mob, @NotNull LivingEntity attacker) {
        super.hurtEnemy(itemStack, mob, attacker);

        if(attacker.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)){
            float gauge = attacker.getExistingData(ModAttachments.SOUL_GAUGE).orElse(0f);

            attacker.setData(ModAttachments.SOUL_GAUGE, Math.min(100, gauge + 4));
        }
    }
}