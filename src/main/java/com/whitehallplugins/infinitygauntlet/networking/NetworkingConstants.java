package com.whitehallplugins.infinitygauntlet.networking;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;

public class NetworkingConstants {

    public static final Identifier GAUNTLET_PACKET_ID = new Identifier(MOD_ID, "update");
    public static final Identifier VERSION_PACKET_ID = new Identifier(MOD_ID, "version");
    public static final String SWAP_POWER_STRING = "swap_power";

    public static int[] modVersion() {
        try {
            if (FabricLoader.getInstance().getModContainer(MOD_ID).isPresent()) {
                String[] versionArrayString = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata()
                        .getVersion().getFriendlyString().split("\\.");
                int[] versionArrayInt = new int[versionArrayString.length];
                for (int i = 0; i < versionArrayString.length; i++) {
                    versionArrayInt[i] = Integer.parseInt(versionArrayString[i]);
                }
                return versionArrayInt;
            }
        } catch (NoSuchElementException e) {
            Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.modversion").getString());
        }
        return null;
    }
}
