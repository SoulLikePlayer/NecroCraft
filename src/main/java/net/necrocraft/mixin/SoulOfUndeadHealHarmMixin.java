package net.necrocraft.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.necrocraft.world.effect.ModMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class SoulOfUndeadHealHarmMixin {

    @Inject(method = "isInvertedHealAndHarm", at = @At("HEAD"), cancellable = true)
    private void necrocraft$invertHealHarmForSoulOfUndead(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModMobEffects.SOUL_OF_UNDEAD)) {
            cir.setReturnValue(true);
        }
    }
}