package com.whitehallplugins.infinitygauntlet.networking.listeners;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GauntletSwapPacketListener implements PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (buf.isReadable()) {
            if (buf.readString().equals(NetworkingConstants.SWAP_POWER_STRING)) {
                if (player.getMainHandStack().getItem() instanceof Gauntlet) {
                    Gauntlet.swapPower(player, player.getMainHandStack());
                }
                else if (player.getOffHandStack().getItem() instanceof Gauntlet) {
                    Gauntlet.swapPower(player, player.getOffHandStack());
                }
            }
        }
    }
}
