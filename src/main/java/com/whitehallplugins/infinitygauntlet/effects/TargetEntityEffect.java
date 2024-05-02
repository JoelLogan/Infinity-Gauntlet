package com.whitehallplugins.infinitygauntlet.effects;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;
import java.util.UUID;

public class TargetEntityEffect extends StatusEffect {

    public TargetEntityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x000000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        try {
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                if (entity instanceof HostileEntity) {
                    for (String s : entity.getCommandTags()) {
                        if (s.split("\\.")[0].equals("MindGemControlled")) {
                            UUID uuid = UUID.fromString(s.split("\\.")[1]);
                            if (Objects.requireNonNull(serverWorld.getEntity(uuid)).isAlive()) {
                                ((HostileEntity) entity).setTarget((LivingEntity) serverWorld.getEntity(uuid));
                            }
                            else {
                                entity.removeStatusEffect(InfinityGauntlet.targetEntityEffect);
                                entity.removeCommandTag(s);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ignored) {}
    }
}