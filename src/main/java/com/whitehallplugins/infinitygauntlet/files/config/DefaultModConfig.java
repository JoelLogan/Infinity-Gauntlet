package com.whitehallplugins.infinitygauntlet.files.config;

import java.util.*;

public final class DefaultModConfig {

    private final List<String> validBooleanVerification = new ArrayList<>();
    private final List<String> validStringListVerification = new ArrayList<>();
    private final Map<String, SimpleConfig.Pair<Integer, Integer>> validIntegerRanges = new HashMap<>();
    private final Map<String, SimpleConfig.Pair<Float, Float>> validFloatRanges = new HashMap<>();

    public List<String> getValidBooleanVerification() {
        return Collections.unmodifiableList(validBooleanVerification);
    }

    public List<String> getValidStringListVerification() {
        return Collections.unmodifiableList(validStringListVerification);
    }

    public Map<String, SimpleConfig.Pair<Integer, Integer>> getValidIntegerRanges() {
        return Collections.unmodifiableMap(validIntegerRanges);
    }

    public Map<String, SimpleConfig.Pair<Float, Float>> getValidFloatRanges() {
        return Collections.unmodifiableMap(validFloatRanges);
    }

    public DefaultModConfig(boolean verifications) {
        SimpleConfig.Pair<Integer, Integer> maxIntegerValue = new SimpleConfig.Pair<>(0, Integer.MAX_VALUE);
        if (verifications) {
            validBooleanVerification.add("isMindGemEnabled");
            validBooleanVerification.add("isMindGemGauntletEnabled");
            validBooleanVerification.add("isPowerGemEnabled");
            validBooleanVerification.add("isPowerGemGauntletEnabled");
            validBooleanVerification.add("isRealityGemEnabled");
            validBooleanVerification.add("isRealityGemGauntletEnabled");
            validBooleanVerification.add("isSoulGemEnabled");
            validBooleanVerification.add("isSoulGemGauntletEnabled");
            validBooleanVerification.add("isSpaceGemEnabled");
            validBooleanVerification.add("isSpaceGemGauntletEnabled");
            validBooleanVerification.add("isTimeGemEnabled");
            validBooleanVerification.add("isTimeGemGauntletEnabled");
            validStringListVerification.add("realityGauntletBlockBlacklist");
            validStringListVerification.add("realityGauntletChangeBlockBlacklist");
            validStringListVerification.add("spaceGauntletWorldChangeOrder");
            validIntegerRanges.put("infinityGauntletBurnTime", new SimpleConfig.Pair<>(0, 72000));
            validFloatRanges.put("infinityGauntletMineSpeed", new SimpleConfig.Pair<>(0f, 2147483647f));
            validIntegerRanges.put("mindGauntletChargeTime", maxIntegerValue);
            validIntegerRanges.put("mindGemMaxAgroDistance", new SimpleConfig.Pair<>(0, 64));
            validIntegerRanges.put("mindGemRarity", maxIntegerValue);
            validIntegerRanges.put("powerGauntletChargeTime", maxIntegerValue);
            validFloatRanges.put("powerGemExplosionPower", new SimpleConfig.Pair<>(0f, 10f));
            validIntegerRanges.put("powerGemBurnTime", new SimpleConfig.Pair<>(0, 72000));
            validIntegerRanges.put("powerGemRarityWarden", maxIntegerValue);
            validIntegerRanges.put("powerGemRarityAncientCity", maxIntegerValue);
            validIntegerRanges.put("realityGauntletChargeTime", maxIntegerValue);
            validIntegerRanges.put("realityGemBlockRadius", new SimpleConfig.Pair<>(0, 64));
            validIntegerRanges.put("realityGemBlockChangeThreadTime", maxIntegerValue);
            validIntegerRanges.put("realityGemConcurrentThreads", maxIntegerValue);
            validIntegerRanges.put("realityGemRarity", maxIntegerValue);
            validIntegerRanges.put("soulGauntletChargeTime", maxIntegerValue);
            validIntegerRanges.put("maxNumberofEntitesInSoulGem", new SimpleConfig.Pair<>(0, 100));
            validIntegerRanges.put("soulGemRarity", maxIntegerValue);
            validIntegerRanges.put("spaceGauntletChargeTime", maxIntegerValue);
            validIntegerRanges.put("spaceGemTeleportCooldown", maxIntegerValue);
            validIntegerRanges.put("spaceGemRarityDragon", maxIntegerValue);
            validIntegerRanges.put("spaceGemRarityEndCity", maxIntegerValue);
            validIntegerRanges.put("spaceGauntletSafeTeleportXAndZ", new SimpleConfig.Pair<>(0, 32));
            validIntegerRanges.put("timeGauntletChargeTime", maxIntegerValue);
            validIntegerRanges.put("timeGemRarity", maxIntegerValue);
            validIntegerRanges.put("raycastEntityDistance", new SimpleConfig.Pair<>(0, 64));
            validIntegerRanges.put("raycastBlocksDistance", new SimpleConfig.Pair<>(0, 64));
            validIntegerRanges.put("raycastCombinedDistance", new SimpleConfig.Pair<>(0, 64));
        }
    }

    public static final String CONFIG_VERSION = "1.0.0";

    public static final boolean IS_MIND_GEM_ENABLED = true;
    public static final boolean IS_MIND_GEM_GAUNTLET_ENABLED = true;
    public static final boolean IS_POWER_GEM_ENABLED = true;
    public static final boolean IS_POWER_GEM_GAUNTLET_ENABLED = true;
    public static final boolean IS_REALITY_GEM_ENABLED = true;
    public static final boolean IS_REALITY_GEM_GAUNTLET_ENABLED = true;
    public static final boolean IS_SOUL_GEM_ENABLED = true;
    public static final boolean IS_SOUL_GEM_GAUNTLET_ENABLED = true;
    public static final boolean IS_SPACE_GEM_ENABLED = true;
    public static final boolean IS_SPACE_GEM_GAUNTLET_ENABLED = true;
    public static final boolean IS_TIME_GEM_ENABLED = true;
    public static final boolean IS_TIME_GEM_GAUNTLET_ENABLED = true;

    public static final int INFINITY_GAUNTLET_BURN_TIME = 1600;
    public static final float INFINITY_GAUNTLET_MINE_SPEED = 200f;

    public static final int MIND_GAUNTLET_CHARGE_TIME = 20;
    public static final int MIND_GEM_MAX_AGRO_DISTANCE = 64;
    public static final int MIND_GEM_RARITY = 3;
    public static final int POWER_GAUNTLET_CHARGE_TIME = 200;
    public static final float POWER_GEM_EXPLOSION_POWER = 4.0f;
    public static final int POWER_GEM_BURN_TIME = 1600;
    public static final int POWER_GEM_RARITY_WARDEN = 50;
    public static final int POWER_GEM_RARITY_ANCIENT_CITY = 15;
    public static final int REALITY_GAUNTLET_CHARGE_TIME = 40;
    public static final int REALITY_GAUNTLET_BLOCK_RADIUS = 32;
    public static final int REALITY_GAUNTLET_CONCURRENT_THREADS = 5;
    public static final int REALITY_GAUNTLET_BLOCK_CHANGE_THREAD_TIME = 25;
    public static final List<String> REALITY_GAUNTLET_TARGET_BLOCK_BLACKLIST = List.of(
            "minecraft:bedrock",
            "minecraft:barrier",
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:structure_block",
            "minecraft:structure_void",
            "minecraft:air",
            "minecraft:cave_air",
            "minecraft:void_air",
            "minecraft:fire",
            "minecraft:soul_fire",
            "minecraft:portal",
            "minecraft:end_portal",
            "minecraft:end_portal_frame",
            "minecraft:nether_portal",
            "minecraft:dragon_egg",
            "minecraft:light");
    public static final List<String> REALITY_GAUNTLET_CHANGE_BLOCK_BLACKLIST = List.of(
            "minecraft:enchanting_table"
    );
    public static final int REALITY_GEM_RARITY = 5;
    public static final int SOUL_GAUNTLET_CHARGE_TIME = 40;
    public static final int MAX_NUMBER_OF_ENTITIES_IN_SOUL_GEM = 25;
    public static final int SOUL_GEM_RARITY = 5;
    public static final int SPACE_GAUNTLET_CHARGE_TIME = 20;
    public static final int SPACE_GEM_TELEPORT_COOLDOWN = 250;
    public static final int SPACE_GEM_RARITY_DRAGON = 5;
    public static final int SPACE_GEM_RARITY_END_CITY = 4;
    public static final int SPACE_GAUNTLET_SAFE_TELEPORT_X_AND_Z = 4;
    public static final List<String> SPACE_GAUNTLET_WORLD_CHANGE_ORDER = List.of(
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
    );
    public static final int TIME_GAUNTLET_CHARGE_TIME = 40;
    public static final int TIME_GEM_RARITY = 3;

    public static final int RAYCAST_ENTITY_DISTANCE = 64;
    public static final int RAYCAST_BLOCKS_DISTANCE = 64;
    public static final int RAYCAST_COMBINED_DISTANCE = 64;

    public static String getConfig(String filename) {
        return  "# InfinityGauntlet Configuration File: " + filename + "\n" +
                "configVersion=" + CONFIG_VERSION + "\n\n" +
                "# All configurable rarities are 1 out of the number chance to spawn.\n\n" +
                "# isMindGemEnabled: Determines if the Mind Gem is enabled. [true/false]\n" +
                "isMindGemEnabled=" + IS_MIND_GEM_ENABLED + "\n" +
                "# isMindGemGauntletEnabled: Determines if the Mind Gem Gauntlet is enabled. [true/false]\n" +
                "isMindGemGauntletEnabled=" + IS_MIND_GEM_GAUNTLET_ENABLED + "\n" +
                "# isPowerGemEnabled: Determines if the Power Gem is enabled. [true/false]\n" +
                "isPowerGemEnabled=" + IS_POWER_GEM_ENABLED + "\n" +
                "# isPowerGemGauntletEnabled: Determines if the Power Gem Gauntlet is enabled. [true/false]\n" +
                "isPowerGemGauntletEnabled=" + IS_POWER_GEM_GAUNTLET_ENABLED + "\n" +
                "# isRealityGemEnabled: Determines if the Reality Gem is enabled. [true/false]\n" +
                "isRealityGemEnabled=" + IS_REALITY_GEM_ENABLED + "\n" +
                "# isRealityGemGauntletEnabled: Determines if the Reality Gem Gauntlet is enabled. [true/false]\n" +
                "isRealityGemGauntletEnabled=" + IS_REALITY_GEM_GAUNTLET_ENABLED + "\n" +
                "# isSoulGemEnabled: Determines if the Soul Gem is enabled. [true/false]\n" +
                "isSoulGemEnabled=" + IS_SOUL_GEM_ENABLED + "\n" +
                "# isSoulGemGauntletEnabled: Determines if the Soul Gem Gauntlet is enabled. [true/false]\n" +
                "isSoulGemGauntletEnabled=" + IS_SOUL_GEM_GAUNTLET_ENABLED + "\n" +
                "# isSpaceGemEnabled: Determines if the Space Gem is enabled. [true/false]\n" +
                "isSpaceGemEnabled=" + IS_SPACE_GEM_ENABLED + "\n" +
                "# isSpaceGemGauntletEnabled: Determines if the Space Gem Gauntlet is enabled. [true/false]\n" +
                "isSpaceGemGauntletEnabled=" + IS_SPACE_GEM_GAUNTLET_ENABLED + "\n" +
                "# isTimeGemEnabled: Determines if the Time Gem is enabled. [true/false]\n" +
                "isTimeGemEnabled=" + IS_TIME_GEM_ENABLED + "\n" +
                "# isTimeGemGauntletEnabled: Determines if the Time Gem Gauntlet is enabled. [true/false]\n" +
                "isTimeGemGauntletEnabled=" + IS_TIME_GEM_GAUNTLET_ENABLED + "\n\n" +
                "# infinityGauntletBurnTime: Determines the burn time in ticks of the Infinity Gauntlet. [0-72000]\n" +
                "infinityGauntletBurnTime=" + INFINITY_GAUNTLET_BURN_TIME + "\n" +
                "# infinityGauntletMineSpeed: Determines the mine speed multiplier of the Infinity Gauntlet. [0.0-2147483647.0]\n" +
                "infinityGauntletMineSpeed=" + INFINITY_GAUNTLET_MINE_SPEED + "\n\n" +
                "# mindGauntletChargeTime: Determines the charge time in ticks of the Mind Gauntlet. [0-2147483647]\n" +
                "mindGauntletChargeTime=" + MIND_GAUNTLET_CHARGE_TIME + "\n" +
                "# mindGemMaxAgroDistance: Determines the maximum agro distance of mobs controlled by the Mind Gem. [0-64]\n" +
                "mindGemMaxAgroDistance=" + MIND_GEM_MAX_AGRO_DISTANCE + "\n" +
                "# mindGemRarity: Determines the rarity of the Mind Gem. [0-2147483647]\n" +
                "mindGemRarity=" + MIND_GEM_RARITY + "\n" +
                "# powerGauntletChargeTime: Determines the charge time in ticks of the Power Gauntlet. [0-2147483647]\n" +
                "powerGauntletChargeTime=" + POWER_GAUNTLET_CHARGE_TIME + "\n" +
                "# powerGemExplosionPower: Determines the explosion power of the Power Gem. [0.0-10.0]\n" +
                "powerGemExplosionPower=" + POWER_GEM_EXPLOSION_POWER + "\n" +
                "# powerGemBurnTime: Determines the burn time in ticks of the Power Gem. [0-72000]\n" +
                "powerGemBurnTime=" + POWER_GEM_BURN_TIME + "\n" +
                "# powerGemRarityWarden: Determines the rarity of the Power Gem from a Warden. [0-2147483647]\n" +
                "powerGemRarityWarden=" + POWER_GEM_RARITY_WARDEN + "\n" +
                "# powerGemRarityAncientCity: Determines the rarity of the Power Gem from a City. [0-2147483647]\n" +
                "powerGemRarityAncientCity=" + POWER_GEM_RARITY_ANCIENT_CITY + "\n" +
                "# realityGauntletChargeTime: Determines the charge time in ticks of the Reality Gauntlet. [0-2147483647]\n" +
                "realityGauntletChargeTime=" + REALITY_GAUNTLET_CHARGE_TIME + "\n" +
                "# realityGauntletBlockRadius: Determines the block radius of the Reality Gem. [0-64]\n" +
                "realityGauntletBlockRadius=" + REALITY_GAUNTLET_BLOCK_RADIUS + "\n" +
                "# realityGauntletBlockChangeThreadTime: Determines the delay in ms between each thread tick of the Reality Gem. [0-2147483647]\n" +
                "realityGauntletBlockChangeThreadTime=" + REALITY_GAUNTLET_BLOCK_CHANGE_THREAD_TIME + "\n" +
                "# realityGauntletConcurrentThreads: Determines the number of concurrent block change threads of the Reality Gem. [0-2147483647]\n" +
                "realityGauntletConcurrentThreads=" + REALITY_GAUNTLET_CONCURRENT_THREADS + "\n" +
                "# realityGauntletTargetBlockBlacklist: Determines the target block change blacklist of the Reality Gem. [minecraft:block, minecraft:otherblock]\n" +
                "realityGauntletTargetBlockBlacklist=" + REALITY_GAUNTLET_TARGET_BLOCK_BLACKLIST + "\n" +
                "# realityGauntletChangeBlockBlacklist: Determines the held block change blacklist of the Reality Gem. [minecraft:block, minecraft:otherblock]\n" +
                "realityGauntletChangeBlockBlacklist=" + REALITY_GAUNTLET_CHANGE_BLOCK_BLACKLIST + "\n" +
                "# realityGemRarity: Determines the rarity of the Reality Gem. [0-2147483647]\n" +
                "realityGemRarity=" + REALITY_GEM_RARITY + "\n" +
                "# soulGauntletChargeTime: Determines the charge time in ticks of the Soul Gauntlet. [0-2147483647]\n" +
                "soulGauntletChargeTime=" + SOUL_GAUNTLET_CHARGE_TIME + "\n" +
                "# maxNumberofEntitesInSoulGem: Determines the maximum number of entities in the Soul Gem. [0-100]\n" +
                "maxNumberofEntitesInSoulGem=" + MAX_NUMBER_OF_ENTITIES_IN_SOUL_GEM + "\n" +
                "# soulGemRarity: Determines the rarity of the Soul Gem. [0-2147483647]\n" +
                "soulGemRarity=" + SOUL_GEM_RARITY + "\n" +
                "# spaceGauntletChargeTime: Determines the charge time in ticks of the Space Gauntlet. [0-2147483647]\n" +
                "spaceGauntletChargeTime=" + SPACE_GAUNTLET_CHARGE_TIME + "\n" +
                "# spaceGemTeleportCooldown: Determines the teleport cooldown in ms of the Space Gem. [0-2147483647]\n" +
                "spaceGemTeleportCooldown=" + SPACE_GEM_TELEPORT_COOLDOWN + "\n" +
                "# spaceGemRarityDragon: Determines the rarity of the Space Gem from an Ender Dragon. [0-2147483647]\n" +
                "spaceGemRarityDragon=" + SPACE_GEM_RARITY_DRAGON + "\n" +
                "# spaceGemRarityEndCity: Determines the rarity of the Space Gem from an End City. [0-2147483647]\n" +
                "spaceGemRarityEndCity=" + SPACE_GEM_RARITY_END_CITY + "\n" +
                "# spaceGauntletSafeTeleportXAndZ: Determines how wide the X and Z axis safe block lookup is for the Space Gauntlet. [0-32]\n" +
                "spaceGauntletSafeTeleportXAndZ=" + SPACE_GAUNTLET_SAFE_TELEPORT_X_AND_Z + "\n" +
                "# spaceGauntletWorldChangeOrder: Determines the world change order of the Space Gauntlet. [minecraft:overworld, minecraft:the_nether]\n" +
                "spaceGauntletWorldChangeOrder=" + SPACE_GAUNTLET_WORLD_CHANGE_ORDER + "\n" +
                "# timeGauntletChargeTime: Determines the charge time in ticks of the Time Gauntlet. [0-2147483647]\n" +
                "timeGauntletChargeTime=" + TIME_GAUNTLET_CHARGE_TIME + "\n" +
                "# timeGemRarity: Determines the rarity of the Time Gem. [0-2147483647]\n" +
                "timeGemRarity=" + TIME_GEM_RARITY + "\n\n" +
                "# raycastEntityDistance: Determines the raycast entity distance. [0-64]\n" +
                "raycastEntityDistance=" + RAYCAST_ENTITY_DISTANCE + "\n" +
                "# raycastBlocksDistance: Determines the raycast blocks distance. [0-64]\n" +
                "raycastBlocksDistance=" + RAYCAST_BLOCKS_DISTANCE + "\n" +
                "# raycastCombinedDistance: Determines the combined raycast distance. [0-64]\n" +
                "raycastCombinedDistance=" + RAYCAST_COMBINED_DISTANCE;
    }
}