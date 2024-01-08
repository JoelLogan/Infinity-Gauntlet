package com.whitehallplugins.infinitygauntlet.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Load;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public class ItemLoadEvent implements Load{
    @Override
    public void onLoad(Entity entity, ServerWorld world) {
        if (entity.getName().toString().contains("item.infinitygauntlet")) {
            entity.setInvulnerable(true);
        }
    }
}
