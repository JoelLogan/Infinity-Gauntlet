package com.whitehallplugins.infinitygauntlet.events;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.*;

public final class PlayerJoinEvent implements Join {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        InfinityGauntlet.addAuthenticatingPlayer(handler.getPlayer());
        unlockRecipes(handler, server);
        sendPacket(handler.getPlayer());
        kickPlayerLater(handler.getPlayer());
    }

    private static void unlockRecipes(ServerPlayNetworkHandler handler, MinecraftServer server) {
        try {
            getItemIdentifiers().forEach(identifier -> {
                String id = identifier.toString();
                if (!id.contains("gauntlet") && !id.contains("replica")) {
                    return;
                }
                Optional<RecipeEntry<?>> recipe = server.getRecipeManager().get(RegistryKey.of(RegistryKeys.RECIPE, identifier));
                if (recipe.isPresent()) {
                    handler.getPlayer().unlockRecipes(Collections.singleton(recipe.orElseThrow()));
                }
            });
        } catch (NoSuchElementException e) {
            Logger.getLogger(MOD_ID).info(Text.translatable("infinitygauntlet.error.recipenotfound").getString());

        }
    }

    private static void sendPacket(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ModVersionPayload(""));
    }

    private static void kickPlayerLater(ServerPlayerEntity player) {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                if (InfinityGauntlet.isPlayerAuthenticating(player)) {
                    player.networkHandler.disconnect(Text.translatable("infinitygauntlet.error.versiontimeout", MOD_ID));
                }
            } catch (InterruptedException e) {
                Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.kicklater", e.toString()).getString());
                Thread.currentThread().interrupt();
            }
        }, executor);
    }
}
