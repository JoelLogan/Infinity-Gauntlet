package com.whitehallplugins.infinitygauntlet.effects;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.CONFIG;

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
            if (entity.getWorld() instanceof ServerWorld serverWorld && entity instanceof HostileEntity) {
                for (String s : entity.getCommandTags()) {
                    if (s.split("\\.")[0].equals("MindGemControlled")) {
                        UUID uuid = UUID.fromString(s.split("\\.")[1]);
                        Entity target = serverWorld.getEntity(uuid);
                        assert target != null;
                        if (target.isAlive() && isCloseEnough(entity, (LivingEntity) target)) {
                            ((HostileEntity) entity).setTarget((LivingEntity) target);
                            if (target.isPlayer()) {
                                if (((PlayerEntity) target).isCreative() || target.isSpectator()){
                                    entity.removeCommandTag(s);
                                    entity.removeStatusEffect(InfinityGauntlet.targetEntityEffect);
                                }
                            }
                        }
                        else {
                            entity.removeCommandTag(s);
                            entity.removeStatusEffect(InfinityGauntlet.targetEntityEffect);
                        }
                    }
                }
            }
        }
        catch (Exception ignored) {}
    }

    private boolean isCloseEnough(LivingEntity entity, LivingEntity target) {
        int range = CONFIG.getOrDefault("mindGemMaxAgroDistance", DefaultModConfig.mindGemMaxAgroDistance);
        return entity.squaredDistanceTo(target) < (range * range);
    }
}