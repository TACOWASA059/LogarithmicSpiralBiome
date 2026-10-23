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
    private static DividerBlockType dividerType = SpiralConfig.DEFAULT_DIVIDER_TYPE;
    private static SpiralPresetStyle style = SpiralPresetStyle.STANDARD;

    private static double scale = SpiralConfig.DEFAULT_SCALE;
    private static double growth = SpiralConfig.DEFAULT_GROWTH;
    private static double spiralRegionWidth = SpiralConfig.DEFAULT_SPIRAL_REGION_WIDTH;
    private static double crossRegionWidth = SpiralConfig.DEFAULT_CROSS_REGION_WIDTH;

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
            if (json.has("divider_type")) {
                dividerType = DividerBlockType.byKey(json.get("divider_type").getAsString());
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
            json.addProperty("divider_type", dividerType.getKey());
            json.addProperty("style", style.name().toLowerCase());
            json.addProperty("scale", scale);
            json.addProperty("growth", growth);
            json.addProperty("spiral_region_width", spiralRegionWidth);
            json.addProperty("cross_region_width", crossRegionWidth);
            json.addProperty("description", "enable_overworld / enable_nether: true/false. divider_blocks: true/false. wall_height: 0-10. divider_type: stone, stone_bricks, deepslate, glass, obsidian, quartz, sea_lantern. style: standard, large, dense, tight, custom.");

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
                dividerType
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

    public static DividerBlockType getDividerType() {
        return dividerType;
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
}
