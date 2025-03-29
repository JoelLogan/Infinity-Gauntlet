package com.whitehallplugins.infinitygauntlet.client;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.payloads.GauntletSwapPayload;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer.MISC_OVERLAYS;

public final class InfinityGauntletClient implements ClientModInitializer {

    private static final Identifier EFFECT_LAYER = Identifier.of(InfinityGauntlet.MOD_ID, "effect-layer");
    boolean isKeyPressed = false;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);
        //HudLayerRegistrationCallback.EVENT.register(layeredDrawerWrapper -> layeredDrawerWrapper.attachLayerBefore(MISC_OVERLAYS, EFFECT_LAYER, this::render));
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

//    public void render(DrawContext context, RenderTickCounter tickCounter) {
//        context.drawText(MinecraftClient.getInstance().textRenderer, "Hello, World!", 50, 50, 0xFFFFFF, true);
//    }

}
