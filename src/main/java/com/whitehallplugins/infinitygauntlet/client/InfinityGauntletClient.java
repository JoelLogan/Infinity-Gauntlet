package com.whitehallplugins.infinitygauntlet.client;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.events.TooltipUpdateEvent;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.payloads.GauntletSwapPayload;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer.MISC_OVERLAYS;

public final class InfinityGauntletClient implements ClientModInitializer {

    private static final Identifier EFFECT_LAYER = Identifier.of(InfinityGauntlet.MOD_ID, "effect-layer");
    private static long animationStartTime = -1;
    private static final long FADE_IN_DURATION = 50;
    private static final long FADE_OUT_DURATION = 1500;
    private static final long TOTAL_DURATION = FADE_IN_DURATION + FADE_OUT_DURATION;
    private static int animationColor = 0x000000; // Default color (black)
    boolean isKeyPressed = false;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);
        TooltipUpdateEvent.EVENT.register(new TooltipUpdateEvent());
        HudLayerRegistrationCallback.EVENT.register(layeredDrawerWrapper -> layeredDrawerWrapper.attachLayerBefore(MISC_OVERLAYS, EFFECT_LAYER, this::render));
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

    /**
     * Triggers the animation with the specified color.
     *
     * @param color The color in hex to use for the animation.
     */
    public static void triggerAnimation(int color) {
        animationColor = color;
        long currentTime = System.currentTimeMillis();
        if (animationStartTime < 0 || currentTime - animationStartTime > TOTAL_DURATION) {
            animationStartTime = currentTime;
        } else {
            animationStartTime = currentTime - (TOTAL_DURATION - FADE_OUT_DURATION);
        }
    }

    private void render(DrawContext context, RenderTickCounter tickCounter) {
        if (animationStartTime < 0) {
            return; // No animation triggered
        }

        long elapsedTime = System.currentTimeMillis() - animationStartTime;
        if (elapsedTime > TOTAL_DURATION) {
            animationStartTime = -1; // End animation
            return;
        }

        float alpha;
        if (elapsedTime <= FADE_IN_DURATION) {
            alpha = Math.min(1.0f, (float) elapsedTime / FADE_IN_DURATION);
        } else {
            alpha = 1.0f - (float) (elapsedTime - FADE_IN_DURATION) / FADE_OUT_DURATION;
        }

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int edgeThickness = (int) (Math.min(width, height) * 0.05); // 5% of the smaller dimension

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int distanceToEdge = Math.min(Math.min(x, width - x), Math.min(y, height - y));
                if (distanceToEdge > edgeThickness) {
                    continue;
                }

                float distanceAlpha = (float) (1.0 - (double) distanceToEdge / edgeThickness);
                int pixelAlpha = (int) (alpha * distanceAlpha * 0xFF);
                int color = (pixelAlpha << 24) | animationColor;
                context.fill(x, y, x + 1, y + 1, color);
            }
        }
    }

}
