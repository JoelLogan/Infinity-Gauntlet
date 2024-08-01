package com.whitehallplugins.infinitygauntlet.networking.listeners;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.minecraft.text.Text;

import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.*;

public final class ModVersionListenerServer implements PlayPayloadHandler<ModVersionPayload> {

    @Override
    public void receive(ModVersionPayload payload, ServerPlayNetworking.Context context) {
        try {
            context.server().execute(() -> {
                InfinityGauntlet.removeAuthenticatingPlayer(context.player());
                if (!payload.modVersion().equals(NetworkingConstants.modVersion())) {
                    context.player().networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionmismatch",
                            MOD_ID, payload.modVersion(), NetworkingConstants.modVersion()));
                }
            });
        }
        catch (Exception e) {
            try {
                context.server().execute(() -> context.player().networkHandler.disconnect(Text.translatable("infinitygauntlet.error.modversionpacket", MOD_ID)));
            }
            catch (Exception e2) {
                Logger.getLogger(MOD_ID).warning("Error while handling mod version packet");
            }
        }

    }
}
