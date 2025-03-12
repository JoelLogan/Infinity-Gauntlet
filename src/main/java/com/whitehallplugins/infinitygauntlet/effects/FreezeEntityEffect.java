package com.whitehallplugins.infinitygauntlet.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public final class FreezeEntityEffect extends StatusEffect {

    public FreezeEntityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xffffff);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}