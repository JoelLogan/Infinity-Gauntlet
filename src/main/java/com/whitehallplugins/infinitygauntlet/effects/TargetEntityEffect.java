package com.whitehallplugins.infinitygauntlet.effects;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public final class TargetEntityEffect extends StatusEffect {

    public static final String COMMAND_TAG = "MindGemControlled";

    public TargetEntityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x000000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld() instanceof ServerWorld serverWorld && entity instanceof HostileEntity) {
            for (String s : entity.getCommandTags()) {
                if (s.split("\\.")[0].equals(COMMAND_TAG)) {
                    UUID uuid = UUID.fromString(s.split("\\.")[1]);
                    Entity target = serverWorld.getEntity(uuid);
                    if (target instanceof LivingEntity) {
                        if (target.isAlive() && isCloseEnough(entity, (LivingEntity) target)) {
                            ((HostileEntity) entity).setTarget((LivingEntity) target);
                            if (target.isPlayer() && ((PlayerEntity) target).isCreative() || target.isSpectator()) {
                                removeEffect(entity);
                                return false;
                            }
                        } else {
                            removeEffect(entity);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void removeEffect(LivingEntity entity) {
        for (String tag : entity.getCommandTags()){
            if (tag.startsWith(COMMAND_TAG)){
                entity.removeCommandTag(tag);
                break;
            }
        }
        entity.removeStatusEffect(RegistryEntry.of(InfinityGauntlet.targetEntityEffect));
    }

    private boolean isCloseEnough(LivingEntity entity, LivingEntity target) {
        int range = InfinityGauntlet.CONFIG.getOrDefault("mindGemMaxAgroDistance", DefaultModConfig.MIND_GEM_MAX_AGRO_DISTANCE);
        return entity.squaredDistanceTo(target) < (range * range);
    }
}