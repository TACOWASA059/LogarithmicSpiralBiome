package com.github.tacowasa059.logarithmicspiralbiome.config;

import com.github.tacowasa059.logarithmicspiralbiome.client.gui.SpiralPresetStyle;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.DividerBlockType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpiralModConfig {
    private static final Path CONFIG_PATH = Path.of("config", "logarithmicspiralbiome.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static boolean enableOverworld = true;
    private static boolean enableNether = true;
    private static boolean dividerBlocks = false;
    private static int wallHeight = SpiralConfig.DEFAULT_WALL_HEIGHT;
    private static String overworldDividerBlock = SpiralConfig.DEFAULT_OVERWORLD_DIVIDER_BLOCK;
    private static String netherDividerBlock = SpiralConfig.DEFAULT_NETHER_DIVIDER_BLOCK;
    private static SpiralPresetStyle style = SpiralPresetStyle.STANDARD;

    private static double scale = SpiralConfig.DEFAULT_SCALE;
    private static double growth = SpiralConfig.DEFAULT_GROWTH;
    private static double spiralRegionWidth = SpiralConfig.DEFAULT_SPIRAL_REGION_WIDTH;
    private static double crossRegionWidth = SpiralConfig.DEFAULT_CROSS_REGION_WIDTH;
    private static double spawnElevateRadius = 100.0D;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("enable_overworld")) {
                enableOverworld = json.get("enable_overworld").getAsBoolean();
            }
            if (json.has("enable_nether")) {
                enableNether = json.get("enable_nether").getAsBoolean();
            }
            if (json.has("divider_blocks")) {
                dividerBlocks = json.get("divider_blocks").getAsBoolean();
            }
            if (json.has("wall_height")) {
                wallHeight = json.get("wall_height").getAsInt();
            }
            if (json.has("overworld_divider_block")) {
                overworldDividerBlock = json.get("overworld_divider_block").getAsString();
            }
            if (json.has("nether_divider_block")) {
                netherDividerBlock = json.get("nether_divider_block").getAsString();
            }
            if (json.has("divider_type") && !json.has("overworld_divider_block")) {
                DividerBlockType type = DividerBlockType.byKey(json.get("divider_type").getAsString());
                overworldDividerBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(type.getOverworldState().getBlock()).toString();
                netherDividerBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(type.getNetherState().getBlock()).toString();
            }
            if (json.has("style")) {
                String styleStr = json.get("style").getAsString();
                for (SpiralPresetStyle s : SpiralPresetStyle.values()) {
                    if (s.name().equalsIgnoreCase(styleStr)) {
                        style = s;
                        if (style != SpiralPresetStyle.CUSTOM) {
                            scale = style.scale();
                            growth = style.growth();
                            spiralRegionWidth = style.spiralRegionWidth();
                            crossRegionWidth = style.crossRegionWidth();
                        }
                        break;
                    }
                }
            }
            if (json.has("scale")) {
                scale = json.get("scale").getAsDouble();
                checkIfCustom();
            }
            if (json.has("growth")) {
                growth = json.get("growth").getAsDouble();
                checkIfCustom();
            }
            if (json.has("spiral_region_width")) {
                spiralRegionWidth = json.get("spiral_region_width").getAsDouble();
                checkIfCustom();
            }
            if (json.has("cross_region_width")) {
                crossRegionWidth = json.get("cross_region_width").getAsDouble();
                checkIfCustom();
            }
            if (json.has("spawn_elevate_radius")) {
                spawnElevateRadius = json.get("spawn_elevate_radius").getAsDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkIfCustom() {
        if (style != SpiralPresetStyle.CUSTOM) {
            if (Math.abs(style.scale() - scale) > 0.001D || Math.abs(style.growth() - growth) > 0.001D) {
                style = SpiralPresetStyle.CUSTOM;
            }
        }
    }

    public static void save() {
        try {
            if (CONFIG_PATH.getParent() != null) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            JsonObject json = new JsonObject();
            json.addProperty("enable_overworld", enableOverworld);
            json.addProperty("enable_nether", enableNether);
            json.addProperty("divider_blocks", dividerBlocks);
            json.addProperty("wall_height", wallHeight);
            json.addProperty("overworld_divider_block", overworldDividerBlock);
            json.addProperty("nether_divider_block", netherDividerBlock);
            json.addProperty("style", style.name().toLowerCase());
            json.addProperty("scale", scale);
            json.addProperty("growth", growth);
            json.addProperty("spiral_region_width", spiralRegionWidth);
            json.addProperty("cross_region_width", crossRegionWidth);
            json.addProperty("spawn_elevate_radius", spawnElevateRadius);
            json.addProperty("description", "enable_overworld / enable_nether: true/false. divider_blocks: true/false. wall_height: 0-10. overworld_divider_block / nether_divider_block: any block ID (e.g. minecraft:stone, minecraft:sea_lantern, etc). style: standard, large, dense, tight, custom. spawn_elevate_radius: radius around 0,0 where terrain is elevated above sea level (default 100.0).");

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SpiralConfig getEffectiveConfig() {
        double boundaryHalfWidth = dividerBlocks ? 0.5D : 0.0D;
        return new SpiralConfig(
                scale,
                growth,
                spiralRegionWidth,
                crossRegionWidth,
                boundaryHalfWidth,
                0,
                0,
                SpiralConfig.DEFAULT_MIN_RADIUS,
                wallHeight,
                overworldDividerBlock,
                netherDividerBlock
        );
    }

    public static boolean isEnableOverworld() {
        return enableOverworld;
    }

    public static boolean isEnableNether() {
        return enableNether;
    }

    public static boolean isDividerBlocks() {
        return dividerBlocks;
    }

    public static int getWallHeight() {
        return wallHeight;
    }

    public static String getOverworldDividerBlock() {
        return overworldDividerBlock;
    }

    public static String getNetherDividerBlock() {
        return netherDividerBlock;
    }

    public static DividerBlockType getDividerType() {
        return DividerBlockType.STONE;
    }

    public static SpiralPresetStyle getStyle() {
        return style;
    }

    public static double getScale() {
        return scale;
    }

    public static double getGrowth() {
        return growth;
    }

    public static double getSpiralRegionWidth() {
        return spiralRegionWidth;
    }

    public static double getCrossRegionWidth() {
        return crossRegionWidth;
    }

    public static void setEnableOverworld(boolean value) {
        enableOverworld = value;
    }

    public static void setEnableNether(boolean value) {
        enableNether = value;
    }

    public static void setDividerBlocks(boolean value) {
        dividerBlocks = value;
    }

    public static void setWallHeight(int value) {
        wallHeight = value;
    }

    public static void setOverworldDividerBlock(String value) {
        overworldDividerBlock = value;
    }

    public static void setNetherDividerBlock(String value) {
        netherDividerBlock = value;
    }

    public static void setStyle(SpiralPresetStyle value) {
        style = value;
    }

    public static void setScale(double value) {
        scale = value;
    }

    public static void setGrowth(double value) {
        growth = value;
    }

    public static void setSpiralRegionWidth(double value) {
        spiralRegionWidth = value;
    }

    public static void setCrossRegionWidth(double value) {
        crossRegionWidth = value;
    }

    public static double getSpawnElevateRadius() {
        return spawnElevateRadius;
    }

    public static void setSpawnElevateRadius(double value) {
        spawnElevateRadius = Math.max(0.0D, value);
    }
}
