package com.whitehallplugins.infinitygauntlet.mixins;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class FreezeEntityMixin {
    @Shadow
    public abstract Collection<StatusEffectInstance> getStatusEffects();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (this.getStatusEffects().stream().anyMatch((statusEffectInstance) -> statusEffectInstance.getEffectType().value().equals(InfinityGauntlet.freezeEntityEffect))) {
            ci.cancel();
        }
    }


}