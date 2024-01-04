package com.whitehallplugins.infinitygauntlet;

import com.whitehallplugins.infinitygauntlet.items.Gauntlet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;

public class InfinityGauntlet implements ModInitializer {

    public static final Gauntlet GAUNTLET_ITEM = new Gauntlet(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof().maxDamage(100));
    private final Identifier[] itemIdentifiers = new Identifier[1];

    @Override
    public void onInitialize() {
        itemIdentifiers[0] = new Identifier("infinitygauntlet", "gauntlet");

        Registry.register(Registries.ITEM, itemIdentifiers[0], GAUNTLET_ITEM);

        //CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> mycommand.register(dispatcher));

        //ServerLifecycleEvents.SERVER_STARTED.register(new serverStartCallback());
    }

}
