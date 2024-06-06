package com.whitehallplugins.infinitygauntlet.networking.listeners;

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

public class ModVersionListenerServer implements PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (buf.isReadable()) {
            int[] versionArray = buf.readIntArray();
            int[] expectedVersionArray = NetworkingConstants.modVersion();
            assert expectedVersionArray != null;
            server.execute(() -> {
                authenticatingPlayers.remove(player);
                if (versionArray.length != expectedVersionArray.length || !Arrays.equals(versionArray, expectedVersionArray)) {
                    player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionmismatch",
                            MOD_ID, Arrays.toString(versionArray).replace(",", ".").replace(" ", ""),
                            Arrays.toString(expectedVersionArray).replace(",", ".").replace(" ", "")));
                }
            });
        }
        else {
            server.execute(() -> player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionpacket", MOD_ID)));
        }
    }
}
