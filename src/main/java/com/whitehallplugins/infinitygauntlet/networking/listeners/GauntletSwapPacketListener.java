package com.whitehallplugins.infinitygauntlet.networking.listeners;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class GauntletSwapPacketListener implements PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (buf.isReadable()) {
            if (buf.readString().equals(NetworkingConstants.SWAP_POWER_STRING)) {
                Gauntlet.swapPower(player, player.getStackInHand(getHand(player)));
            }
        }
    }

    private Hand getHand(ServerPlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof Gauntlet ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }
}
