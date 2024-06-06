package com.whitehallplugins.infinitygauntlet.files.config;

import java.util.*;

public class DefaultModConfig {

    private final List<String> VALID_BOOLEAN_VERIFICATION = new ArrayList<>();
    private final List<String> VALID_STRING_LIST_VERIFICATION = new ArrayList<>();
    private final Map<String, SimpleConfig.Pair<Integer, Integer>> VALID_INTEGER_RANGES = new HashMap<>();
    private final Map<String, SimpleConfig.Pair<Float, Float>> VALID_FLOAT_RANGES = new HashMap<>();

    public List<String> getValidBooleanVerification() {
        return Collections.unmodifiableList(VALID_BOOLEAN_VERIFICATION);
    }

    public List<String> getValidStringListVerification() {
        return Collections.unmodifiableList(VALID_STRING_LIST_VERIFICATION);
    }

    public Map<String, SimpleConfig.Pair<Integer, Integer>> getValidIntegerRanges() {
        return Collections.unmodifiableMap(VALID_INTEGER_RANGES);
    }

    public Map<String, SimpleConfig.Pair<Float, Float>> getValidFloatRanges() {
        return Collections.unmodifiableMap(VALID_FLOAT_RANGES);
    }

    public DefaultModConfig(boolean verifications) {
        if (verifications) {
            VALID_BOOLEAN_VERIFICATION.add("isMindGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isMindGemGauntletEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isPowerGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isPowerGemGauntletEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isRealityGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isRealityGemGauntletEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isSoulGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isSoulGemGauntletEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isSpaceGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isSpaceGemGauntletEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isTimeGemEnabled");
            VALID_BOOLEAN_VERIFICATION.add("isTimeGemGauntletEnabled");
            VALID_STRING_LIST_VERIFICATION.add("realityGauntletBlockBlacklist");
            VALID_STRING_LIST_VERIFICATION.add("realityGauntletChangeBlockBlacklist");
            VALID_INTEGER_RANGES.put("infinityGauntletBurnTime", new SimpleConfig.Pair<>(0, 72000));
            VALID_FLOAT_RANGES.put("infinityGauntletMineSpeed", new SimpleConfig.Pair<>(0f, 2147483647f));
            VALID_INTEGER_RANGES.put("mindGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("mindGemMaxAgroDistance", new SimpleConfig.Pair<>(0, 64));
            VALID_INTEGER_RANGES.put("mindGemRarity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("powerGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_FLOAT_RANGES.put("powerGemExplosionPower", new SimpleConfig.Pair<>(0f, 10f));
            VALID_INTEGER_RANGES.put("powerGemBurnTime", new SimpleConfig.Pair<>(0, 72000));
            VALID_INTEGER_RANGES.put("powerGemRarityWarden", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("powerGemRarityAncientCity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("realityGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("realityGemBlockRadius", new SimpleConfig.Pair<>(0, 64));
            VALID_INTEGER_RANGES.put("realityGemBlockChangeThreadTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("realityGemConcurrentThreads", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("realityGemRarity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("soulGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("maxNumberofEntitesInSoulGem", new SimpleConfig.Pair<>(0, 100));
            VALID_INTEGER_RANGES.put("soulGemRarity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("spaceGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("spaceGemTeleportCooldown", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("spaceGemRarityDragon", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("spaceGemRarityEndCity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("timeGauntletChargeTime", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("timeGemRarity", new SimpleConfig.Pair<>(0, Integer.MAX_VALUE));
            VALID_INTEGER_RANGES.put("raycastEntityDistance", new SimpleConfig.Pair<>(0, 64));
            VALID_INTEGER_RANGES.put("raycastBlocksDistance", new SimpleConfig.Pair<>(0, 64));
            VALID_INTEGER_RANGES.put("raycastCombinedDistance", new SimpleConfig.Pair<>(0, 64));
        }
    }

    public static final String configVersion = "1.0.0";

    public static final boolean isMindGemEnabled = true;
    public static final boolean isMindGemGauntletEnabled = true;
    public static final boolean isPowerGemEnabled = true;
    public static final boolean isPowerGemGauntletEnabled = true;
    public static final boolean isRealityGemEnabled = true;
    public static final boolean isRealityGemGauntletEnabled = true;
    public static final boolean isSoulGemEnabled = true;
    public static final boolean isSoulGemGauntletEnabled = true;
    public static final boolean isSpaceGemEnabled = true;
    public static final boolean isSpaceGemGauntletEnabled = true;
    public static final boolean isTimeGemEnabled = true;
    public static final boolean isTimeGemGauntletEnabled = true;

    public static final int infinityGauntletBurnTime = 1600;
    public static final float infinityGauntletMineSpeed = 200f;

    public static final int mindGauntletChargeTime = 20;
    public static final int mindGemMaxAgroDistance = 64;
    public static final int mindGemRarity = 3;
    public static final int powerGauntletChargeTime = 200;
    public static final float powerGemExplosionPower = 4.0f;
    public static final int powerGemBurnTime = 1600;
    public static final int powerGemRarityWarden = 50;
    public static final int powerGemRarityAncientCity = 15;
    public static final int realityGauntletChargeTime = 40;
    public static final int realityGauntletBlockRadius = 32;
    public static final int realityGauntletConcurrentThreads = 5;
    public static final int realityGauntletBlockChangeThreadTime = 25;
    public static final List<String> realityGauntletTargetBlockBlacklist = List.of(
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
            "minecraft:water",
            "minecraft:lava",
            "minecraft:flowing_water",
            "minecraft:flowing_lava",
            "minecraft:fire",
            "minecraft:soul_fire",
            "minecraft:portal",
            "minecraft:end_portal",
            "minecraft:end_portal_frame",
            "minecraft:nether_portal",
            "minecraft:dragon_egg",
            "minecraft:light");
    public static final List<String> realityGauntletChangeBlockBlacklist = List.of(
            "minecraft:enchanting_table"
    );
    public static final int realityGemRarity = 5;
    public static final int soulGauntletChargeTime = 40;
    public static final int maxNumberofEntitesInSoulGem = 25;
    public static final int soulGemRarity = 5;
    public static final int spaceGauntletChargeTime = 20;
    public static final int spaceGemTeleportCooldown = 250;
    public static final int spaceGemRarityDragon = 5;
    public static final int spaceGemRarityEndCity = 4;
    public static final int timeGauntletChargeTime = 40;
    public static final int timeGemRarity = 3;

    public static final int raycastEntityDistance = 64;
    public static final int raycastBlocksDistance = 64;
    public static final int raycastCombinedDistance = 64;

    public static String getConfig(String filename) {
         return  "# InfinityGauntlet Configuration File: " + filename + "\n" +
                 "configVersion=" + configVersion + "\n\n" +
                 "# All configurable rarities are 1 out of the number chance to spawn.\n\n" +
                 "# isMindGemEnabled: Determines if the Mind Gem is enabled. [true/false]\n" +
                 "isMindGemEnabled=" + isMindGemEnabled + "\n" +
                 "# isMindGemGauntletEnabled: Determines if the Mind Gem Gauntlet is enabled. [true/false]\n" +
                 "isMindGemGauntletEnabled=" + isMindGemGauntletEnabled + "\n" +
                 "# isPowerGemEnabled: Determines if the Power Gem is enabled. [true/false]\n" +
                 "isPowerGemEnabled=" + isPowerGemEnabled + "\n" +
                 "# isPowerGemGauntletEnabled: Determines if the Power Gem Gauntlet is enabled. [true/false]\n" +
                 "isPowerGemGauntletEnabled=" + isPowerGemGauntletEnabled + "\n" +
                 "# isRealityGemEnabled: Determines if the Reality Gem is enabled. [true/false]\n" +
                 "isRealityGemEnabled=" + isRealityGemEnabled + "\n" +
                 "# isRealityGemGauntletEnabled: Determines if the Reality Gem Gauntlet is enabled. [true/false]\n" +
                 "isRealityGemGauntletEnabled=" + isRealityGemGauntletEnabled + "\n" +
                 "# isSoulGemEnabled: Determines if the Soul Gem is enabled. [true/false]\n" +
                 "isSoulGemEnabled=" + isSoulGemEnabled + "\n" +
                 "# isSoulGemGauntletEnabled: Determines if the Soul Gem Gauntlet is enabled. [true/false]\n" +
                 "isSoulGemGauntletEnabled=" + isSoulGemGauntletEnabled + "\n" +
                 "# isSpaceGemEnabled: Determines if the Space Gem is enabled. [true/false]\n" +
                 "isSpaceGemEnabled=" + isSpaceGemEnabled + "\n" +
                 "# isSpaceGemGauntletEnabled: Determines if the Space Gem Gauntlet is enabled. [true/false]\n" +
                 "isSpaceGemGauntletEnabled=" + isSpaceGemGauntletEnabled + "\n" +
                 "# isTimeGemEnabled: Determines if the Time Gem is enabled. [true/false]\n" +
                 "isTimeGemEnabled=" + isTimeGemEnabled + "\n" +
                 "# isTimeGemGauntletEnabled: Determines if the Time Gem Gauntlet is enabled. [true/false]\n" +
                 "isTimeGemGauntletEnabled=" + isTimeGemGauntletEnabled + "\n\n" +
                 "# infinityGauntletBurnTime: Determines the burn time in ticks of the Infinity Gauntlet. [0-72000]\n" +
                 "infinityGauntletBurnTime=" + infinityGauntletBurnTime + "\n" +
                 "# infinityGauntletMineSpeed: Determines the mine speed multiplier of the Infinity Gauntlet. [0.0-2147483647.0]\n" +
                 "infinityGauntletMineSpeed=" + infinityGauntletMineSpeed + "\n\n" +
                 "# mindGauntletChargeTime: Determines the charge time in ticks of the Mind Gauntlet. [0-2147483647]\n" +
                 "mindGauntletChargeTime=" + mindGauntletChargeTime + "\n" +
                 "# mindGemMaxAgroDistance: Determines the maximum agro distance of mobs controlled by the Mind Gem. [0-64]\n" +
                 "mindGemMaxAgroDistance=" + mindGemMaxAgroDistance + "\n" +
                 "# mindGemRarity: Determines the rarity of the Mind Gem. [0-2147483647]\n" +
                 "mindGemRarity=" + mindGemRarity + "\n" +
                 "# powerGauntletChargeTime: Determines the charge time in ticks of the Power Gauntlet. [0-2147483647]\n" +
                 "powerGauntletChargeTime=" + powerGauntletChargeTime + "\n" +
                 "# powerGemExplosionPower: Determines the explosion power of the Power Gem. [0.0-10.0]\n" +
                 "powerGemExplosionPower=" + powerGemExplosionPower + "\n" +
                 "# powerGemBurnTime: Determines the burn time in ticks of the Power Gem. [0-72000]\n" +
                 "powerGemBurnTime=" + powerGemBurnTime + "\n" +
                 "# powerGemRarityWarden: Determines the rarity of the Power Gem from a Warden. [0-2147483647]\n" +
                 "powerGemRarityWarden=" + powerGemRarityWarden + "\n" +
                 "# powerGemRarityAncientCity: Determines the rarity of the Power Gem from a City. [0-2147483647]\n" +
                 "powerGemRarityAncientCity=" + powerGemRarityAncientCity + "\n" +
                 "# realityGauntletChargeTime: Determines the charge time in ticks of the Reality Gauntlet. [0-2147483647]\n" +
                 "realityGauntletChargeTime=" + realityGauntletChargeTime + "\n" +
                 "# realityGauntletBlockRadius: Determines the block radius of the Reality Gem. [0-64]\n" +
                 "realityGauntletBlockRadius=" + realityGauntletBlockRadius + "\n" +
                 "# realityGauntletBlockChangeThreadTime: Determines the delay in ms between each thread tick of the Reality Gem. [0-2147483647]\n" +
                 "realityGauntletBlockChangeThreadTime=" + realityGauntletBlockChangeThreadTime + "\n" +
                 "# realityGauntletConcurrentThreads: Determines the number of concurrent block change threads of the Reality Gem. [0-2147483647]\n" +
                 "realityGauntletConcurrentThreads=" + realityGauntletConcurrentThreads + "\n" +
                 "# realityGauntletTargetBlockBlacklist: Determines the target block change blacklist of the Reality Gem. [minecraft:block, minecraft:otherblock]\n" +
                 "realityGauntletTargetBlockBlacklist=" + realityGauntletTargetBlockBlacklist + "\n" +
                 "# realityGauntletChangeBlockBlacklist: Determines the held block change blacklist of the Reality Gem. [minecraft:block, minecraft:otherblock]\n" +
                 "realityGauntletChangeBlockBlacklist=" + realityGauntletChangeBlockBlacklist + "\n" +
                 "# realityGemRarity: Determines the rarity of the Reality Gem. [0-2147483647]\n" +
                 "realityGemRarity=" + realityGemRarity + "\n" +
                 "# soulGauntletChargeTime: Determines the charge time in ticks of the Soul Gauntlet. [0-2147483647]\n" +
                 "soulGauntletChargeTime=" + soulGauntletChargeTime + "\n" +
                 "# maxNumberofEntitesInSoulGem: Determines the maximum number of entities in the Soul Gem. [0-100]\n" +
                 "maxNumberofEntitesInSoulGem=" + maxNumberofEntitesInSoulGem + "\n" +
                 "# soulGemRarity: Determines the rarity of the Soul Gem. [0-2147483647]\n" +
                 "soulGemRarity=" + soulGemRarity + "\n" +
                 "# spaceGauntletChargeTime: Determines the charge time in ticks of the Space Gauntlet. [0-2147483647]\n" +
                 "spaceGauntletChargeTime=" + spaceGauntletChargeTime + "\n" +
                 "# spaceGemTeleportCooldown: Determines the teleport cooldown in ms of the Space Gem. [0-2147483647]\n" +
                 "spaceGemTeleportCooldown=" + spaceGemTeleportCooldown + "\n" +
                 "# spaceGemRarityDragon: Determines the rarity of the Space Gem from an Ender Dragon. [0-2147483647]\n" +
                 "spaceGemRarityDragon=" + spaceGemRarityDragon + "\n" +
                 "# spaceGemRarityEndCity: Determines the rarity of the Space Gem from an End City. [0-2147483647]\n" +
                 "spaceGemRarityEndCity=" + spaceGemRarityEndCity + "\n" +
                 "# timeGauntletChargeTime: Determines the charge time in ticks of the Time Gauntlet. [0-2147483647]\n" +
                 "timeGauntletChargeTime=" + timeGauntletChargeTime + "\n" +
                 "# timeGemRarity: Determines the rarity of the Time Gem. [0-2147483647]\n" +
                 "timeGemRarity=" + timeGemRarity + "\n\n" +
                 "# raycastEntityDistance: Determines the raycast entity distance. [0-64]\n" +
                 "raycastEntityDistance=" + raycastEntityDistance + "\n" +
                 "# raycastBlocksDistance: Determines the raycast blocks distance. [0-64]\n" +
                 "raycastBlocksDistance=" + raycastBlocksDistance + "\n" +
                 "# raycastCombinedDistance: Determines the combined raycast distance. [0-64]\n" +
                 "raycastCombinedDistance=" + raycastCombinedDistance;
    }
}