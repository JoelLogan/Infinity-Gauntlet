package com.whitehallplugins.infinitygauntlet.events;

import net.kyrptonaught.customportalapi.event.PortalIgniteEvent;
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoulDimensionPortalOpenEvent implements PortalIgniteEvent {
    @Override
    public void afterLight(PlayerEntity playerEntity, World world, BlockPos blockPos, BlockPos blockPos1, PortalIgnitionSource portalIgnitionSource) {
//        if (playerEntity.getInventory().getMainHandStack().getItem().equals(InfinityGauntlet.SOUL_DIMENSION_PORTAL_IGNITION_ITEM)) {
//            playerEntity.getInventory().getMainHandStack().decrement(1);
//            playerEntity.getInventory().insertStack(playerEntity.getInventory().selectedSlot, new ItemStack(InfinityGauntlet.SOUL_DIMENSION_PORTAL_POST_LIGHT_ITEM));
//        }
    }
}
