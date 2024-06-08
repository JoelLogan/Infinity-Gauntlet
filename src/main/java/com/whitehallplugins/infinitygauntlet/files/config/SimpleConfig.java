package com.whitehallplugins.infinitygauntlet.files.config;
/*
 * Copyright (c) 2021 magistermaks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.whitehallplugins.infinitygauntlet.files.config.exceptions.ConfigLoadException;
import com.whitehallplugins.infinitygauntlet.files.config.exceptions.InvalidConfigValueException;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;

public final class SimpleConfig {

    private final DefaultModConfig defaultModConfig = new DefaultModConfig(true);
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private final HashMap<String, String> config = new HashMap<>();
    private final ConfigRequest request;
    private boolean broken = false;

    public record Pair<F, S>(F first, S second) {}

    public interface DefaultConfig {
        String get( String namespace );

        static String empty( String namespace ) {
            return "";
        }
    }

    public static class ConfigRequest {

        private final File file;
        private final String filename;
        private DefaultConfig provider;

        private ConfigRequest(File file, String filename ) {
            this.file = file;
            this.filename = filename;
            this.provider = DefaultConfig::empty;
        }

        /**
         * Sets the default config provider, used to generate the
         * config if it's missing.
         *
         * @param provider default config provider
         * @return current config request object
         * @see DefaultConfig
         */
        public ConfigRequest provider( DefaultConfig provider ) {
            this.provider = provider;
            return this;
        }

        /**
         * Loads the config from the filesystem.
         *
         * @return config object
         * @see SimpleConfig
         */
        public SimpleConfig request() {
            return new SimpleConfig( this );
        }

        private String getConfig() {
            return provider.get( filename ) + "\n";
        }

    }

    /**
     * Creates new config request object, ideally `namespace`
     * should be the name of the mod id of the requesting mod
     *
     * @param filename - name of the config file
     * @return new config request object
     */
    public static ConfigRequest of( String filename ) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        return new ConfigRequest( path.resolve( filename + ".properties" ).toFile(), filename );
    }

    private void createConfig() throws IOException {

        // try creating missing files
        if (!request.file.getParentFile().mkdirs()){
            LOGGER.info(MOD_ID + ": Failed to create parent directories for config file! (Probably Already Exists)");
        }
        Files.createFile( request.file.toPath() );

        // write default config data
        PrintWriter writer = new PrintWriter(request.file, StandardCharsets.UTF_8);
        writer.write( request.getConfig() );
        writer.close();

    }

    private void loadConfig() throws IOException {
        Scanner reader = new Scanner( request.file, StandardCharsets.UTF_8 );
        for( int line = 1; reader.hasNextLine(); line ++ ) {
            parseConfigEntry( reader.nextLine(), line );
        }
        reader.close();
    }

    private void parseConfigEntry(String entry, int line) {
        if (!entry.isEmpty() && !entry.startsWith("#")) {
            String[] parts = entry.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];

                if (Objects.equals(key, "configVersion") && !Objects.equals(value, DefaultModConfig.CONFIG_VERSION)) {
                    LOGGER.warn(MOD_ID + ": Config version mismatch! Consider regenerating your config file...");
                }
                String valueKey = ": Value for key '";
                String onLine = "' on line ";
                if (defaultModConfig.getValidBooleanVerification().contains(key) && !Boolean.parseBoolean(value)){
                    throw new InvalidConfigValueException(MOD_ID + valueKey + key + onLine + line + " is not a boolean!");
                }
                else if (defaultModConfig.getValidStringListVerification().contains(key)) {
                    if (!value.startsWith("[") || !value.endsWith("]")) {
                        throw new InvalidConfigValueException(MOD_ID + valueKey + key + onLine + line + " is not a string list!");
                    }
                }
                else if (defaultModConfig.getValidIntegerRanges().containsKey(key)) {
                    Pair<Integer, Integer> range = defaultModConfig.getValidIntegerRanges().get(key);
                    int intValue;
                    try {
                        intValue = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new InvalidConfigValueException(MOD_ID + valueKey + key + onLine + line + " is not an integer!");
                    }
                    if (intValue < range.first() || intValue > range.second()) {
                        throw new InvalidConfigValueException(MOD_ID + ": Value out of range for key '" + key + onLine + line + "!");
                    }
                } else if (defaultModConfig.getValidFloatRanges().containsKey(key)) {
                    Pair<Float, Float> range = defaultModConfig.getValidFloatRanges().get(key);
                    float floatValue;
                    try {
                        floatValue = Float.parseFloat(value);
                    } catch (Exception e) {
                        throw new InvalidConfigValueException(MOD_ID + valueKey + key + onLine + line + " is not a float!");
                    }
                    if (floatValue < range.first() || floatValue > range.second()) {
                        throw new InvalidConfigValueException(MOD_ID + ": Value out of range for key '" + key + onLine + line + "!");
                    }
                }
                config.put(key, value);
            } else {
                throw new InvalidConfigValueException(MOD_ID + ": Syntax error in config file on line " + line + "!");
            }
        }
    }

    private SimpleConfig(ConfigRequest request) {
        this.request = request;
        String identifier = "Config '" + request.filename + "'";

        if( !request.file.exists() ) {
            LOGGER.info(MOD_ID + ": {} is missing, generating default one...", identifier);

            try {
                createConfig();
            } catch (IOException e) {
                LOGGER.error(MOD_ID + ": {} failed to generate!", identifier);
                LOGGER.trace( e );
                broken = true;
            }
        }

        if( !broken ) {
            try {
                loadConfig();
            } catch (Exception e) {
                LOGGER.error(MOD_ID + ": {} failed to load!", identifier);
                LOGGER.trace( e );
                broken = true;
                throw new ConfigLoadException(MOD_ID + ": Failed to load config file! " + e.getMessage());
            }
        }

    }

    /**
     * Queries a value from config, returns `null` if the
     * key does not exist.
     *
     * @return  value corresponding to the given key
     * @see     SimpleConfig#getOrDefault
     * @hidden
     */
    public String get( String key ) {
        return config.get( key );
    }

    /**
     * Returns string value from config corresponding to the given
     * key, or the default string if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public String getOrDefault( String key, String def ) {
        String val = get(key);
        return val == null ? def : val;
    }

    /**
     * Returns integer value from config corresponding to the given
     * key, or the default integer if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public int getOrDefault( String key, int def ) {
        try {
            return Integer.parseInt( get(key) );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns boolean value from config corresponding to the given
     * key, or the default boolean if the key is missing.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public boolean getOrDefault( String key, boolean def ) {
        String val = get(key);
        if( val != null ) {
            return val.equalsIgnoreCase("true");
        }

        return def;
    }

    /**
     * Returns double value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public double getOrDefault( String key, double def ) {
        try {
            return Double.parseDouble( get(key) );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns float value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public float getOrDefault( String key, float def ) {
        try {
            return Float.parseFloat( get(key) );
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns String List value from config corresponding to the given
     * key, or the default string if the key is missing or invalid.
     *
     * @return  value corresponding to the given key, or the default value
     */
    @SuppressWarnings("unused")
    public List<String> getOrDefault( String key, List<String> def ) {
        try {
            return List.of(get(key));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * If any error occurred during loading or reading from the config
     * a 'broken' flag is set, indicating that the config's state
     * is undefined and should be discarded using `delete()`
     *
     * @return the 'broken' flag of the configuration
     */
    @SuppressWarnings("unused")
    public boolean isBroken() {
        return broken;
    }

    /**
     * deletes the config file from the filesystem
     *
     * @return true if the operation was successful
     */
    @SuppressWarnings("unused")
    public boolean delete() {
        LOGGER.warn(MOD_ID + ": Config '{}' was removed from existence! Restart the game to regenerate it.", request.filename);
        return request.file.delete();
    }
}
