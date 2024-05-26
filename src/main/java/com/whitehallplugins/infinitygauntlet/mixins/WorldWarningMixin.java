package com.whitehallplugins.infinitygauntlet.mixins;

import net.minecraft.server.integrated.IntegratedServerLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(IntegratedServerLoader.class)
public abstract class WorldWarningMixin {

    @ModifyVariable(method = "start(Lnet/minecraft/world/level/storage/LevelStorage$Session;Lcom/mojang/serialization/Dynamic;ZZLjava/lang/Runnable;)V",
            at = @At("HEAD"), argsOnly = true, index = 4)
    private boolean removeWarningOnLoad(boolean original) {
        return false;
    }

    @ModifyVariable(method = "tryLoad", at = @At("HEAD"), argsOnly = true, index = 4)
    private static boolean removeWarningOnCreation(boolean original) {
        return true;
    }
}