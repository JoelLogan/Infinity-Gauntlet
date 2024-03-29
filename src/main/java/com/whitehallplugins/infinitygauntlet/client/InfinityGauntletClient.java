package com.whitehallplugins.infinitygauntlet.client;

import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class InfinityGauntletClient implements ClientModInitializer {

    boolean isKeyPressed = false;
    PacketByteBuf swapPowerPacket = PacketByteBufs.create().writeString(NetworkingConstants.SWAP_POWER_STRING);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (InfinityGauntletKeybinds.CHANGE_POWER.isPressed()) {
                if (!isKeyPressed) {
                    ClientPlayNetworking.send(NetworkingConstants.MOD_PACKET_ID, swapPowerPacket);
                    isKeyPressed = true;
                }
            } else {
                isKeyPressed = false;
            }
        });
    }
}
