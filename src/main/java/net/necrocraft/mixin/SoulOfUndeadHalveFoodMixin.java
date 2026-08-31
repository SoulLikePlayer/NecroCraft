package net.necrocraft.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.necrocraft.world.effect.ModMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class SoulOfUndeadHalveFoodMixin {

    @Unique
    private ServerPlayer necrocraft$owningPlayer;

    @Inject(method = "tick", at = @At("HEAD"))
    private void necrocraft$captureOwner(ServerPlayer player, CallbackInfo ci){
        necrocraft$owningPlayer = player;
    }

    @ModifyVariable(method = "eat(IF)V", at = @At("HEAD"), argsOnly = true, name = "food")
    private int necrocraft$halveFoodLevel(int food) {
        if (necrocraft$owningPlayer != null && necrocraft$owningPlayer.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
            return food / 2;
        }

        return food;
    }

    @ModifyVariable(method = "eat(IF)V", at = @At("HEAD"), argsOnly = true, name = "saturationModifier")
    private float necrocraft$halveSaturation(float saturationModifier) {
        if (necrocraft$owningPlayer != null && necrocraft$owningPlayer.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
            return saturationModifier / 2f;
        }
        return saturationModifier;
    }
}
