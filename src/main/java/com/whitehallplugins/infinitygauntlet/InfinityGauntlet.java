package com.whitehallplugins.infinitygauntlet;

import com.whitehallplugins.infinitygauntlet.client.InfinityGauntletKeybinds;
import com.whitehallplugins.infinitygauntlet.events.ItemLoadEvent;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.GauntletReplica;
import com.whitehallplugins.infinitygauntlet.items.gems.replicas.*;
import com.whitehallplugins.infinitygauntlet.items.gems.*;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.ServerPacketListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;

public class InfinityGauntlet implements ModInitializer {


    /**
     * {@code @todo} Add infinity stones
     * {@code @TODO} Add infinity stone effects
     * {@code @TODO} Get gauntlet and stone models
     * {@code @TODO} Add fakes
     */
    public static final Gauntlet GAUNTLET_ITEM = new Gauntlet(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof().maxDamage(100));
    public static final MindGem MIND_GEM = new MindGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final PowerGem POWER_GEM = new PowerGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final RealityGem REALITY_GEM = new RealityGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final SoulGem SOUL_GEM = new SoulGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final SpaceGem SPACE_GEM = new SpaceGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final TimeGem TIME_GEM = new TimeGem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final GauntletReplica GAUNTLET_REPLICA_ITEM = new GauntletReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final MindGemReplica MIND_GEM_REPLICA = new MindGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final PowerGemReplica POWER_GEM_REPLICA = new PowerGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final RealityGemReplica REALITY_GEM_REPLICA = new RealityGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final SoulGemReplica SOUL_GEM_REPLICA = new SoulGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final SpaceGemReplica SPACE_GEM_REPLICA = new SpaceGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());
    public static final TimeGemReplica TIME_GEM_REPLICA = new TimeGemReplica(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1).fireproof());

    private final Identifier[] itemIdentifiers = new Identifier[14];

    @Override
    public void onInitialize() {
        itemIdentifiers[0] = new Identifier("infinitygauntlet", "gauntlet/gauntlet");
        itemIdentifiers[1] = new Identifier("infinitygauntlet", "mind/gem");
        itemIdentifiers[2] = new Identifier("infinitygauntlet", "power/gem");
        itemIdentifiers[3] = new Identifier("infinitygauntlet", "reality/gem");
        itemIdentifiers[4] = new Identifier("infinitygauntlet", "soul/gem");
        itemIdentifiers[5] = new Identifier("infinitygauntlet", "space/gem");
        itemIdentifiers[6] = new Identifier("infinitygauntlet", "time/gem");
        itemIdentifiers[7] = new Identifier("infinitygauntlet", "gauntlet/gauntletreplica");
        itemIdentifiers[8] = new Identifier("infinitygauntlet", "mind/gemreplica");
        itemIdentifiers[9] = new Identifier("infinitygauntlet", "power/gemreplica");
        itemIdentifiers[10] = new Identifier("infinitygauntlet", "reality/gemreplica");
        itemIdentifiers[11] = new Identifier("infinitygauntlet", "soul/gemreplica");
        itemIdentifiers[12] = new Identifier("infinitygauntlet", "space/gemreplica");
        itemIdentifiers[13] = new Identifier("infinitygauntlet", "time/gemreplica");

        Registry.register(Registries.ITEM, itemIdentifiers[0], GAUNTLET_ITEM);
        Registry.register(Registries.ITEM, itemIdentifiers[1], MIND_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[2], POWER_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[3], REALITY_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[4], SOUL_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[5], SPACE_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[6], TIME_GEM);
        Registry.register(Registries.ITEM, itemIdentifiers[7], GAUNTLET_REPLICA_ITEM);
        Registry.register(Registries.ITEM, itemIdentifiers[8], MIND_GEM_REPLICA);
        Registry.register(Registries.ITEM, itemIdentifiers[9], POWER_GEM_REPLICA);
        Registry.register(Registries.ITEM, itemIdentifiers[10], REALITY_GEM_REPLICA);
        Registry.register(Registries.ITEM, itemIdentifiers[11], SOUL_GEM_REPLICA);
        Registry.register(Registries.ITEM, itemIdentifiers[12], SPACE_GEM_REPLICA);
        Registry.register(Registries.ITEM, itemIdentifiers[13], TIME_GEM_REPLICA);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(GAUNTLET_REPLICA_ITEM);
            content.add(MIND_GEM_REPLICA);
            content.add(POWER_GEM_REPLICA);
            content.add(REALITY_GEM_REPLICA);
            content.add(SOUL_GEM_REPLICA);
            content.add(SPACE_GEM_REPLICA);
            content.add(TIME_GEM_REPLICA);
        });

        KeyBindingHelper.registerKeyBinding(InfinityGauntletKeybinds.CHANGE_POWER);

        //CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> mycommand.register(dispatcher));

        //ServerLifecycleEvents.SERVER_STARTED.register(new serverStartCallback());

        ServerEntityEvents.ENTITY_LOAD.register(new ItemLoadEvent());

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.MOD_PACKET_ID, new ServerPacketListener());
    }

}
