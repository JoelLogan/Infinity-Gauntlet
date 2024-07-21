package com.whitehallplugins.infinitygauntlet.networking.listeners;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.payloads.GauntletSwapPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public final class GauntletSwapPacketListener implements PlayPayloadHandler<GauntletSwapPayload> {

    @Override
    public void receive(GauntletSwapPayload payload, ServerPlayNetworking.Context context) {
        if (payload.swapPowerString().equals(NetworkingConstants.SWAP_POWER_STRING)) {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> Gauntlet.swapPower(player, player.getStackInHand(getHand(player))));
        }
    }

    private Hand getHand(ServerPlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof Gauntlet ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }
}
