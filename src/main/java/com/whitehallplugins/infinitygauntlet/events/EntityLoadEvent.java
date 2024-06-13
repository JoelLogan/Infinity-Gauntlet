package com.whitehallplugins.infinitygauntlet.events;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Load;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.Objects;

public class EntityLoadEvent implements Load {
    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if (entity.getName().toString().contains("item.infinitygauntlet")) {
            entity.setInvulnerable(true);
            ((ItemEntity) entity).setNeverDespawn();
        }
        else if (entity.getType().equals(EntityType.AREA_EFFECT_CLOUD)) {
            NbtCompound compound = new NbtCompound();
            entity.saveNbt(compound);
            if (compound.contains("effects") && Objects.requireNonNull(compound.get("effects")).toString().contains(InfinityGauntlet.TARGET_ENTITY_EFFECT.toString())) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
