package com.whitehallplugins.infinitygauntlet.mixins;

import com.whitehallplugins.infinitygauntlet.files.teleport.OfflineTeleportManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void onPlayerConnectPre(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        MinecraftServer server = player.getServer();
        UUID playerUUID = player.getUuid();
        assert server != null;
        ServerPlayerEntity existingPlayer = server.getPlayerManager().getPlayer(playerUUID);
        if (existingPlayer != null) {
            existingPlayer.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.loggedinfromanotherlocation"));
            server.getPlayerManager().remove(existingPlayer);
        }
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        UUID playerUUID = player.getUuid();
        NbtCompound teleportData = OfflineTeleportManager.getTeleportData(playerUUID);

        if (teleportData != null) {
            double targetX = teleportData.getDouble("TargetX");
            double targetY = teleportData.getDouble("TargetY");
            double targetZ = teleportData.getDouble("TargetZ");
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(teleportData.getString("World")));

            ServerWorld targetWorld = Objects.requireNonNull(player.getServer()).getWorld(worldKey);
            if (targetWorld != null) {
                Vec3d targetPos = new Vec3d(targetX, targetY, targetZ);
                player.teleport(targetWorld, targetPos.x, targetPos.y, targetPos.z, player.getYaw(), player.getPitch());
            }
            player.setSpawnPoint(worldKey, new BlockPos((int) targetX, (int) targetY, (int) targetZ), 0.0F, true, false);
            OfflineTeleportManager.removeTeleportData(playerUUID);
        }
    }
}
