package com.whitehallplugins.infinitygauntlet;

import com.whitehallplugins.infinitygauntlet.commands.StopRealityThreads;
import com.whitehallplugins.infinitygauntlet.effects.FreezeEntityEffect;
import com.whitehallplugins.infinitygauntlet.effects.TargetEntityEffect;
import com.whitehallplugins.infinitygauntlet.events.EntityLoadEvent;
import com.whitehallplugins.infinitygauntlet.events.LootTableModifyEvent;
import com.whitehallplugins.infinitygauntlet.events.PlayerJoinEvent;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import com.whitehallplugins.infinitygauntlet.files.teleport.OfflineTeleportManager;
import com.whitehallplugins.infinitygauntlet.files.config.SimpleConfig;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.GauntletReplica;
import com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions;
import com.whitehallplugins.infinitygauntlet.items.gems.replicas.ReplicaGems.*;
import com.whitehallplugins.infinitygauntlet.items.gems.Gems.*;
import com.whitehallplugins.infinitygauntlet.networking.listeners.GauntletSwapPacketListener;
import com.whitehallplugins.infinitygauntlet.networking.listeners.ModVersionListenerServer;
import com.whitehallplugins.infinitygauntlet.networking.payloads.GauntletSwapPayload;
import com.whitehallplugins.infinitygauntlet.networking.payloads.ModVersionPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.block.AbstractBlock;
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
import net.minecraft.world.World;

import java.util.*;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.initThreadShutdownHook;

public final class InfinityGauntlet implements ModInitializer {
    
    public static final String MOD_ID = "infinitygauntlet";

    private static final Identifier[] itemIdentifiers = {
            Identifier.of(MOD_ID, "gauntlet/gauntlet"),
            Identifier.of(MOD_ID, "mind/gem"),
            Identifier.of(MOD_ID, "power/gem"),
            Identifier.of(MOD_ID, "reality/gem"),
            Identifier.of(MOD_ID, "soul/gem"),
            Identifier.of(MOD_ID, "space/gem"),
            Identifier.of(MOD_ID, "time/gem"),
            Identifier.of(MOD_ID, "gauntlet/gauntletreplica"),
            Identifier.of(MOD_ID, "mind/gemreplica"),
            Identifier.of(MOD_ID, "power/gemreplica"),
            Identifier.of(MOD_ID, "reality/gemreplica"),
            Identifier.of(MOD_ID, "soul/gemreplica"),
            Identifier.of(MOD_ID, "space/gemreplica"),
            Identifier.of(MOD_ID, "time/gemreplica"),
            Identifier.of(MOD_ID, "souldimensionblock")
    };

    private static final Identifier[] blockIdentifiers = {
            Identifier.of(MOD_ID, "souldimensionblock")
    };

    public static final Gauntlet GAUNTLET_ITEM = new Gauntlet(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().maxDamage(100).registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[0])));
    public static final MindGem MIND_GEM = new MindGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[1])));
    public static final PowerGem POWER_GEM = new PowerGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[2])));
    public static final RealityGem REALITY_GEM = new RealityGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[3])));
    public static final SoulGem SOUL_GEM = new SoulGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[4])));
    public static final SpaceGem SPACE_GEM = new SpaceGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[5])));
    public static final TimeGem TIME_GEM = new TimeGem(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[6])));
    public static final GauntletReplica GAUNTLET_REPLICA_ITEM = new GauntletReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[7])));
    public static final MindGemReplica MIND_GEM_REPLICA = new MindGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[8])));
    public static final PowerGemReplica POWER_GEM_REPLICA = new PowerGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[9])));
    public static final RealityGemReplica REALITY_GEM_REPLICA = new RealityGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[10])));
    public static final SoulGemReplica SOUL_GEM_REPLICA = new SoulGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[11])));
    public static final SpaceGemReplica SPACE_GEM_REPLICA = new SpaceGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[12])));
    public static final TimeGemReplica TIME_GEM_REPLICA = new TimeGemReplica(new Item.Settings().rarity(Rarity.EPIC).maxCount(1).fireproof().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[13])));

    private static final List<PlayerEntity> authenticatingPlayers = new ArrayList<>();

    private static Set<RegistryKey<World>> serverWorlds;

    public static final Identifier SOUL_DIMENSION_ID = Identifier.of(MOD_ID, "souldimension");
    public static final Identifier TARGET_ENTITY_EFFECT_ID = Identifier.of(MOD_ID, "targeteffect");
    public static final Identifier FREEZE_ENTITY_EFFECT_ID = Identifier.of(MOD_ID, "freezeeffect");

    public static final Block SOUL_DIMENSION_BLOCK = new Block(AbstractBlock.Settings.create().strength(-1.0f, 3600000.0F).dropsNothing().registryKey(RegistryKey.of(RegistryKeys.BLOCK, blockIdentifiers[0])));

    public static final StatusEffect targetEntityEffect = new TargetEntityEffect();
    public static final StatusEffect freezeEntityEffect = new FreezeEntityEffect();

    public static final RegistryKey<DamageType> POWER_GEM_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "power_gem_damage_type"));

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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> StopRealityThreads.register(dispatcher));

        FuelRegistryEvents.BUILD.register((builder, context) -> builder.add(POWER_GEM, CONFIG.getOrDefault("powerGemBurnTime",
                DefaultModConfig.POWER_GEM_BURN_TIME) + 5));
        FuelRegistryEvents.BUILD.register((builder, context) -> builder.add(GAUNTLET_ITEM, CONFIG.getOrDefault("infinityGauntletBurnTime",
                DefaultModConfig.INFINITY_GAUNTLET_BURN_TIME) + 5));


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> content.add(SOUL_DIMENSION_BLOCK.asItem()));

        Registry.register(Registries.BLOCK, blockIdentifiers[0], SOUL_DIMENSION_BLOCK);
        Registry.register(Registries.ITEM, itemIdentifiers[14], new BlockItem(SOUL_DIMENSION_BLOCK, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, itemIdentifiers[14]))));

        Registry.register(Registries.STATUS_EFFECT, TARGET_ENTITY_EFFECT_ID, targetEntityEffect);
        Registry.register(Registries.STATUS_EFFECT, FREEZE_ENTITY_EFFECT_ID, freezeEntityEffect);

        PayloadTypeRegistry.playS2C().register(ModVersionPayload.ID, ModVersionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ModVersionPayload.ID, ModVersionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GauntletSwapPayload.ID, GauntletSwapPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ModVersionPayload.ID, new ModVersionListenerServer());
        ServerPlayNetworking.registerGlobalReceiver(GauntletSwapPayload.ID, new GauntletSwapPacketListener());

        ServerPlayConnectionEvents.JOIN.register(new PlayerJoinEvent());
        LootTableEvents.MODIFY.register(new LootTableModifyEvent());
        ServerEntityEvents.ENTITY_LOAD.register(new EntityLoadEvent());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            serverWorlds = server.getWorldRegistryKeys();
            SharedGemFunctions.setKeepRunning(true);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            OfflineTeleportManager.saveTeleportData();
            SharedGemFunctions.setKeepRunning(false);
        });
    }

    private static void registerItems() {
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

    public static List<Identifier> getItemIdentifiers() {
        return Collections.unmodifiableList(Arrays.asList(itemIdentifiers));
    }

    public static Set<RegistryKey<World>> getServerWorlds() {
        return Set.copyOf(serverWorlds);
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
