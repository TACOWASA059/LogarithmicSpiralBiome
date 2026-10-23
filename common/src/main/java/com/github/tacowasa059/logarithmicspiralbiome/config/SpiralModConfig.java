package com.github.tacowasa059.logarithmicspiralbiome.config;

import com.github.tacowasa059.logarithmicspiralbiome.client.gui.SpiralPresetStyle;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
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

    private static SpiralPresetStyle style = SpiralPresetStyle.STANDARD;
    private static boolean dividerBlocks = false;
    private static double customScale = -1.0D;
    private static double customGrowth = -1.0D;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("style")) {
                String styleStr = json.get("style").getAsString();
                for (SpiralPresetStyle s : SpiralPresetStyle.values()) {
                    if (s.name().equalsIgnoreCase(styleStr)) {
                        style = s;
                        break;
                    }
                }
            }
            if (json.has("divider_blocks")) {
                dividerBlocks = json.get("divider_blocks").getAsBoolean();
            }
            if (json.has("scale")) {
                customScale = json.get("scale").getAsDouble();
            }
            if (json.has("growth")) {
                customGrowth = json.get("growth").getAsDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            if (CONFIG_PATH.getParent() != null) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            JsonObject json = new JsonObject();
            json.addProperty("style", style.name().toLowerCase());
            json.addProperty("divider_blocks", dividerBlocks);
            json.addProperty("description", "Styles: standard, large, dense, tight. divider_blocks: true/false. scale/growth: optional custom overrides.");
            if (customScale > 0.0D) {
                json.addProperty("scale", customScale);
            }
            if (customGrowth > 0.0D) {
                json.addProperty("growth", customGrowth);
            }
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(json, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SpiralConfig getEffectiveConfig() {
        SpiralConfig base = style.toConfig(dividerBlocks);
        double scale = customScale > 0.0D ? customScale : base.scale();
        double growth = customGrowth > 0.0D ? customGrowth : base.growth();
        return new SpiralConfig(
                scale,
                growth,
                base.spiralRegionWidth(),
                base.crossRegionWidth(),
                base.boundaryHalfWidth(),
                base.centerX(),
                base.centerZ(),
                base.minRadius()
        );
    }

    public static SpiralPresetStyle getStyle() {
        return style;
    }

    public static boolean isDividerBlocks() {
        return dividerBlocks;
    }
}
