package com.whitehallplugins.infinitygauntlet.client;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class InfinityGauntletClient implements ClientModInitializer {

    boolean isKeyPressed = false;
    PacketByteBuf swapPowerPacket = PacketByteBufs.create().writeString(NetworkingConstants.SWAP_POWER_STRING);

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (InfinityGauntletKeybinds.CHANGE_POWER.isPressed()) {
                if (!isKeyPressed) {
                    assert client.player != null;
                    if (client.player.getMainHandStack().getItem() instanceof Gauntlet || client.player.getOffHandStack().getItem() instanceof Gauntlet){
                        ClientPlayNetworking.send(NetworkingConstants.GAUNTLET_PACKET_ID, swapPowerPacket);
                    }
                    isKeyPressed = true;
                }
            } else {
                isKeyPressed = false;
            }
        });
    }
}
