package com.whitehallplugins.infinitygauntlet.client;

import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.payloads.GauntletSwapPayload;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class InfinityGauntletClient implements ClientModInitializer {

    boolean isKeyPressed = false;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (InfinityGauntletKeybinds.CHANGE_POWER.isPressed()) {
                if (!isKeyPressed && client.player != null) {
                    if (client.player.getMainHandStack().getItem() instanceof Gauntlet || client.player.getOffHandStack().getItem() instanceof Gauntlet){
                        ClientPlayNetworking.send(new GauntletSwapPayload(NetworkingConstants.SWAP_POWER_STRING));
                    }
                    isKeyPressed = true;
                }
            } else {
                isKeyPressed = false;
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(ModVersionPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        context.responseSender().sendPacket(new ModVersionPayload(NetworkingConstants.modVersion()))));
    }
}
