package com.whitehallplugins.infinitygauntlet.events;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.*;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents.Modify;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class LootTableModifyEvent implements Modify {

    private static final Identifier WITHER_LOOT_TABLE_ID = EntityType.WITHER.getLootTableId();
    private static final Identifier WARDEN_LOOT_TABLE_ID = EntityType.WARDEN.getLootTableId();
    private static final Identifier ENDER_DRAGON_LOOT_TABLE_ID = EntityType.ENDER_DRAGON.getLootTableId();
    private static final Identifier JUNGLE_TEMPLE_LOOT_TABLE_ID = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier ANCIENT_CITY_LOOT_TABLE_ID = new Identifier("minecraft", "chests/ancient_city");
    private static final Identifier DESERT_PYRAMID_LOOT_TABLE_ID = new Identifier("minecraft", "chests/desert_pyramid");
    private static final Identifier END_CITY_LOOT_TABLE_ID = new Identifier("minecraft", "chests/end_city_treasure");
    private static final Identifier WOODLAND_MANSION_LOOT_TABLE_ID = new Identifier("minecraft", "chests/woodland_mansion");

    @Override
    public void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        if (source.isBuiltin()) {
            if (WITHER_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(SOUL_GEM.asItem(),
                        CONFIG.getOrDefault("soulGemRarity", DefaultModConfig.soulGemRarity));
                tableBuilder.pool(poolBuilder);
            } else if (WARDEN_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(POWER_GEM.asItem(),
                        CONFIG.getOrDefault("powerGemRarityWarden", DefaultModConfig.powerGemRarityWarden));
                tableBuilder.pool(poolBuilder);
            } else if (ENDER_DRAGON_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(SPACE_GEM.asItem(),
                        CONFIG.getOrDefault("spaceGemRarityDragon", DefaultModConfig.spaceGemRarityDragon));
                tableBuilder.pool(poolBuilder);
            } else if (ANCIENT_CITY_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(POWER_GEM.asItem(),
                        CONFIG.getOrDefault("powerGemRarityCity", DefaultModConfig.powerGemRarityAncientCity));
                tableBuilder.pool(poolBuilder);
            } else if (JUNGLE_TEMPLE_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(MIND_GEM.asItem(),
                        CONFIG.getOrDefault("mindGemRarity", DefaultModConfig.mindGemRarity));
                tableBuilder.pool(poolBuilder);
            } else if (DESERT_PYRAMID_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(TIME_GEM.asItem(),
                        CONFIG.getOrDefault("timeGemRarity", DefaultModConfig.timeGemRarity));
                tableBuilder.pool(poolBuilder);
            } else if (END_CITY_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(SPACE_GEM.asItem(),
                        CONFIG.getOrDefault("spaceGemRarityEndCity", DefaultModConfig.spaceGemRarityEndCity));
                tableBuilder.pool(poolBuilder);
            } else if (WOODLAND_MANSION_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = getPoolBuilder(REALITY_GEM.asItem(),
                        CONFIG.getOrDefault("realityGemRarity", DefaultModConfig.realityGemRarity));
                tableBuilder.pool(poolBuilder);
            }
        }
    }

    private static LootPool.Builder getPoolBuilder(Item gem, int weightMax) {
        return LootPool.builder()
                .with(ItemEntry.builder(gem)
                        .weight(1))
                .with(EmptyEntry.builder().weight(weightMax));
    }
}
