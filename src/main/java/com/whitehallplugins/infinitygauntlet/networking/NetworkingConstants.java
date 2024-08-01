package com.whitehallplugins.infinitygauntlet.networking;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;

public final class NetworkingConstants {

    public static final Identifier GAUNTLET_PACKET_ID = new Identifier(MOD_ID, "update");
    public static final Identifier VERSION_PACKET_ID = new Identifier(MOD_ID, "version");
    public static final String SWAP_POWER_STRING = "swap_power";

    private NetworkingConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static String modVersion() {
        try {
            if (FabricLoader.getInstance().getModContainer(MOD_ID).isPresent()) {
                return FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata()
                        .getVersion().getFriendlyString();
            }
        } catch (NoSuchElementException e) {
            Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.modversion").getString());
        }
        return "0.0.0";
    }
}
