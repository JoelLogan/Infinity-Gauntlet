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

import java.util.Objects;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.*;

public final class ModVersionListenerServer implements PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (buf.isReadable()) {
            String versionArray = buf.readString();
            String expectedVersionArray = NetworkingConstants.modVersion();
            server.execute(() -> {
                InfinityGauntlet.removeAuthenticatingPlayer(player);
                if (!Objects.equals(versionArray, expectedVersionArray)) {
                    player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionmismatch",
                            MOD_ID, versionArray, expectedVersionArray));
                }
            });
        }
        else {
            server.execute(() -> player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionpacket", MOD_ID)));
        }
    }
}
