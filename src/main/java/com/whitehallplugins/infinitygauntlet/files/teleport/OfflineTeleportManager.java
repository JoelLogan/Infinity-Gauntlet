package com.whitehallplugins.infinitygauntlet.files.teleport;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;

public class OfflineTeleportManager {

    private static final File TELEPORT_DATA_FILE = new File(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/offline_teleport_data.json").toString());
    private static final Map<UUID, NbtCompound> teleportDataMap = new HashMap<>();

    public static void loadTeleportData() {
        if (!TELEPORT_DATA_FILE.exists()) {
            return;
        }
        try (Reader reader = new FileReader(TELEPORT_DATA_FILE, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                NbtCompound nbt = NbtUtils.fromJson(entry.getValue().getAsJsonObject());
                teleportDataMap.put(uuid, nbt);
            }
        } catch (IOException e) {
            Logger.getLogger(MOD_ID).warning("Error loading teleport data: " + e.getMessage());
        }
    }

    public static void saveTeleportData() {
        JsonObject json = new JsonObject();
        for (Map.Entry<UUID, NbtCompound> entry : teleportDataMap.entrySet()) {
            json.add(entry.getKey().toString(), NbtUtils.toJson(entry.getValue()));
        }
        try (Writer writer = new FileWriter(TELEPORT_DATA_FILE, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, writer);
        } catch (IOException e) {
            Logger.getLogger(MOD_ID).warning("Error saving teleport data: " + e.getMessage());
        }
    }

    public static void setTeleportData(UUID uuid, NbtCompound teleportData) {
        teleportDataMap.put(uuid, teleportData);
        saveTeleportData();
    }

    public static NbtCompound getTeleportData(UUID uuid) {
        return teleportDataMap.get(uuid);
    }

    public static void removeTeleportData(UUID uuid) {
        teleportDataMap.remove(uuid);
        saveTeleportData();
    }
}
