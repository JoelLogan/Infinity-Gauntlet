package com.whitehallplugins.infinitygauntlet.files.teleport;

import net.minecraft.nbt.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Objects;

public class NbtUtils {
    public static JsonObject toJson(NbtCompound nbt) {
        JsonObject json = new JsonObject();
        for (String key : nbt.getKeys()) {
            JsonElement value = toJsonElement(Objects.requireNonNull(nbt.get(key)));
            json.add(key, value);
        }
        return json;
    }

    private static JsonElement toJsonElement(NbtElement element) {
        return switch (element.getType()) {
            case NbtElement.COMPOUND_TYPE -> toJson((NbtCompound) element);
            case NbtElement.STRING_TYPE -> new JsonPrimitive(element.asString());
            case NbtElement.INT_TYPE -> new JsonPrimitive(((NbtInt) element).intValue());
            case NbtElement.DOUBLE_TYPE -> new JsonPrimitive(((NbtDouble) element).doubleValue());
            case NbtElement.LONG_TYPE -> new JsonPrimitive(((NbtLong) element).longValue());
            default -> throw new UnsupportedOperationException("Unsupported NBT element type: " + element.getType());
        };
    }

    public static NbtCompound fromJson(JsonObject json) {
        NbtCompound nbt = new NbtCompound();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            nbt.put(entry.getKey(), fromJsonElement(entry.getValue()));
        }
        return nbt;
    }

    private static NbtElement fromJsonElement(JsonElement element) {
        if (element.isJsonObject()) {
            return fromJson(element.getAsJsonObject());
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                if (primitive.getAsString().contains(".")) {
                    return NbtDouble.of(primitive.getAsDouble());
                } else {
                    return NbtInt.of(primitive.getAsInt());
                }
            } else if (primitive.isString()) {
                return NbtString.of(primitive.getAsString());
            }
        }
        throw new UnsupportedOperationException("Unsupported JSON element: " + element);
    }
}
