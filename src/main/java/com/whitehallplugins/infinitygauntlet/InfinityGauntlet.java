package com.whitehallplugins.infinitygauntlet;

import com.whitehallplugins.infinitygauntlet.client.InfinityGauntletKeybinds;
import com.whitehallplugins.infinitygauntlet.items.Gauntlet;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.ServerPacketListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;

public class InfinityGauntlet implements ModInitializer {


    /**
     * {@code @todo} Add infinity stones
     * {@code @TODO} Add infinity stone effects
     * {@code @TODO} Get gauntlet model
     */
    public static final Gauntlet GAUNTLET_ITEM = new Gauntlet(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof().maxDamage(100));
    private final Identifier[] itemIdentifiers = new Identifier[1];

    @Override
    public void onInitialize() {
        itemIdentifiers[0] = new Identifier("infinitygauntlet", "gauntlet");

        Registry.register(Registries.ITEM, itemIdentifiers[0], GAUNTLET_ITEM);

        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);

        //CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> mycommand.register(dispatcher));

        //ServerLifecycleEvents.SERVER_STARTED.register(new serverStartCallback());

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.MOD_PACKET_ID, new ServerPacketListener());
    }

}
