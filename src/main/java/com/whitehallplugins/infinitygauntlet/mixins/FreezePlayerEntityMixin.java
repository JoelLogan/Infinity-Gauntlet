package com.whitehallplugins.infinitygauntlet.mixins;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class FreezePlayerEntityMixin {

    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    private void stopBlockInteract(CallbackInfoReturnable<Double> cir) {
        if (isEffectActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(0.0D);
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
    private void stopEntityInteract(CallbackInfoReturnable<Double> cir) {
        if (isEffectActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(0.0D);
        }
    }

    @Unique
    private boolean isEffectActive(PlayerEntity player) {
        return player.getStatusEffects().stream().anyMatch((statusEffectInstance) -> statusEffectInstance.getEffectType().value().equals(InfinityGauntlet.freezeEntityEffect));
    }
}