package com.whitehallplugins.infinitygauntlet.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public final class FreezeEntityEffect extends StatusEffect {

    public FreezeEntityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xffffff);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        return true;
    }


}