package net.necrocraft.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.necrocraft.world.effect.ModMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class SoulOfUndeadNaturalRegenMixin {

    @Unique
    private float necrocraft$healthBeforeTick;

    @Inject(method = "tick", at = @At("HEAD"))
    private void necrocraft$captureHealth(ServerPlayer player, CallbackInfo ci) {
        necrocraft$healthBeforeTick = player.getHealth();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void necrocraft$blockNaturalRegen(ServerPlayer player, CallbackInfo ci) {
        if (!player.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) return;

        if (player.getHealth() > necrocraft$healthBeforeTick) {
            player.setHealth(necrocraft$healthBeforeTick);
        }
    }
}