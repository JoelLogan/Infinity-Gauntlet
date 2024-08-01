package com.whitehallplugins.infinitygauntlet.networking.payloads;

import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ModVersionPayload(String modVersion) implements CustomPayload {

    public static final CustomPayload.Id<ModVersionPayload> ID = new CustomPayload.Id<>(NetworkingConstants.VERSION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ModVersionPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, ModVersionPayload::modVersion, ModVersionPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
