package com.whitehallplugins.infinitygauntlet.networking.payloads;

import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record GauntletSwapPayload(String swapPowerString) implements CustomPayload {

    public static final CustomPayload.Id<GauntletSwapPayload> ID = new CustomPayload.Id<>(NetworkingConstants.GAUNTLET_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, GauntletSwapPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, GauntletSwapPayload::swapPowerString, GauntletSwapPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
