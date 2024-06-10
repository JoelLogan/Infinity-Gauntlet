package com.whitehallplugins.infinitygauntlet;

import com.whitehallplugins.infinitygauntlet.effects.TargetEntityEffect;
import com.whitehallplugins.infinitygauntlet.events.EntityLoadEvent;
import com.whitehallplugins.infinitygauntlet.events.LootTableModifyEvent;
import com.whitehallplugins.infinitygauntlet.events.PlayerJoinEvent;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import com.whitehallplugins.infinitygauntlet.files.teleport.OfflineTeleportManager;
import com.whitehallplugins.infinitygauntlet.files.config.SimpleConfig;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.GauntletReplica;
import com.whitehallplugins.infinitygauntlet.items.gems.replicas.ReplicaGems.*;
import com.whitehallplugins.infinitygauntlet.items.gems.Gems.*;
import com.whitehallplugins.infinitygauntlet.networking.NetworkingConstants;
import com.whitehallplugins.infinitygauntlet.networking.listeners.GauntletSwapPacketListener;
import com.whitehallplugins.infinitygauntlet.networking.listeners.ModVersionListenerServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.initThreadShutdownHook;

public final class InfinityGauntlet implements ModInitializer {

    /**
     * TODO: Get gauntlet models
     */
    
    public static final String MOD_ID = "infinitygauntlet";

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

    private static final Identifier[] itemIdentifiers = new Identifier[14];

    private static final List<PlayerEntity> authenticatingPlayers = new ArrayList<>();

    public static final Identifier SOUL_DIMENSION = new Identifier(MOD_ID, "souldimension");
    public static final Identifier TARGET_ENTITY_EFFECT = new Identifier(MOD_ID, "targeteffect");

    public static final Block SOUL_DIMENSION_BLOCK = new Block(FabricBlockSettings.create().strength(-1.0f, 3600000.0F).dropsNothing());

    public static final StatusEffect targetEntityEffect = new TargetEntityEffect();

    public static final RegistryKey<DamageType> POWER_GEM_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MOD_ID, "power_gem_damage_type"));

    public static final SimpleConfig CONFIG = SimpleConfig.of("IGConfig").provider(DefaultModConfig::getConfig).request();

    @Override
    public void onInitialize() {
        OfflineTeleportManager.loadTeleportData();

        registerItems();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(GAUNTLET_REPLICA_ITEM);
            content.add(MIND_GEM_REPLICA);
            content.add(POWER_GEM_REPLICA);
            content.add(REALITY_GEM_REPLICA);
            content.add(SOUL_GEM_REPLICA);
            content.add(SPACE_GEM_REPLICA);
            content.add(TIME_GEM_REPLICA);
        });

        initThreadShutdownHook();

        FuelRegistry.INSTANCE.add(POWER_GEM, CONFIG.getOrDefault("powerGemBurnTime",
                DefaultModConfig.POWER_GEM_BURN_TIME) + 5);
        FuelRegistry.INSTANCE.add(GAUNTLET_ITEM, CONFIG.getOrDefault("infinityGauntletBurnTime",
                DefaultModConfig.INFINITY_GAUNTLET_BURN_TIME) + 5);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> content.add(SOUL_DIMENSION_BLOCK.asItem()));

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "souldimensionblock"), SOUL_DIMENSION_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "souldimensionblock"), new BlockItem(SOUL_DIMENSION_BLOCK, new Item.Settings()));

        Registry.register(Registries.STATUS_EFFECT, TARGET_ENTITY_EFFECT, targetEntityEffect);

        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.GAUNTLET_PACKET_ID, new GauntletSwapPacketListener());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.VERSION_PACKET_ID, new ModVersionListenerServer());

        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        LootTableEvents.MODIFY.register(new LootTableModifyEvent());
        ServerEntityEvents.ENTITY_LOAD.register(new EntityLoadEvent());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> OfflineTeleportManager.saveTeleportData());
    }

    private static void registerItems() {
        itemIdentifiers[0] = new Identifier(MOD_ID, "gauntlet/gauntlet");
        itemIdentifiers[1] = new Identifier(MOD_ID, "mind/gem");
        itemIdentifiers[2] = new Identifier(MOD_ID, "power/gem");
        itemIdentifiers[3] = new Identifier(MOD_ID, "reality/gem");
        itemIdentifiers[4] = new Identifier(MOD_ID, "soul/gem");
        itemIdentifiers[5] = new Identifier(MOD_ID, "space/gem");
        itemIdentifiers[6] = new Identifier(MOD_ID, "time/gem");
        itemIdentifiers[7] = new Identifier(MOD_ID, "gauntlet/gauntletreplica");
        itemIdentifiers[8] = new Identifier(MOD_ID, "mind/gemreplica");
        itemIdentifiers[9] = new Identifier(MOD_ID, "power/gemreplica");
        itemIdentifiers[10] = new Identifier(MOD_ID, "reality/gemreplica");
        itemIdentifiers[11] = new Identifier(MOD_ID, "soul/gemreplica");
        itemIdentifiers[12] = new Identifier(MOD_ID, "space/gemreplica");
        itemIdentifiers[13] = new Identifier(MOD_ID, "time/gemreplica");

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
    }

    public static Identifier gauntletIdentifier() {
        return itemIdentifiers[0];
    }

    public static void addAuthenticatingPlayer(PlayerEntity player) {
        authenticatingPlayers.add(player);
    }

    public static void removeAuthenticatingPlayer(PlayerEntity player) {
        authenticatingPlayers.remove(player);
    }

    public static boolean isPlayerAuthenticating(PlayerEntity player) {
        return authenticatingPlayers.contains(player);
    }
}
