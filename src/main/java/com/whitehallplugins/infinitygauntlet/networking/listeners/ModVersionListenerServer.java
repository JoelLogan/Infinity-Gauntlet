package com.whitehallplugins.infinitygauntlet.networking.listeners;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.*;

public final class ModVersionListenerServer implements PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (buf.isReadable()) {
            int[] versionArray = buf.readIntArray();
            int[] expectedVersionArray = NetworkingConstants.modVersion();
            server.execute(() -> {
                InfinityGauntlet.removeAuthenticatingPlayer(player);
                if (versionArray.length != expectedVersionArray.length || !Arrays.equals(versionArray, expectedVersionArray)) {
                    player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionmismatch",
                            MOD_ID, getModVersionString(versionArray), getModVersionString(expectedVersionArray)));
                }
            });
        }
        else {
            server.execute(() -> player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionpacket", MOD_ID)));
        }
    }

    private String getModVersionString(int[] versionArray) {
        StringBuilder versionString = new StringBuilder();
        for (int i = 0; i < versionArray.length; i++) {
            versionString.append(versionArray[i]);
            if (i < versionArray.length - 1) {
                versionString.append(".");
            }
        }
        return versionString.toString();
    }
}
