package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import net.minecraft.network.chat.Component;

public enum SpiralPresetStyle {
    STANDARD("standard", 3.0D, 0.32D, Math.PI / 8.0D, Math.PI / 5.0D),
    LARGE("large", 6.0D, 0.35D, Math.PI / 8.0D, Math.PI / 5.0D),
    DENSE("dense", 1.5D, 0.32D, Math.PI / 12.0D, Math.PI / 8.0D),
    TIGHT("tight", 2.0D, 0.18D, Math.PI / 6.0D, Math.PI / 5.0D);

    private final String key;
    private final double scale;
    private final double growth;
    private final double spiralRegionWidth;
    private final double crossRegionWidth;

    SpiralPresetStyle(String key, double scale, double growth, double spiralRegionWidth, double crossRegionWidth) {
        this.key = key;
        this.scale = scale;
        this.growth = growth;
        this.spiralRegionWidth = spiralRegionWidth;
        this.crossRegionWidth = crossRegionWidth;
    }

    public Component displayName() {
        return Component.translatable("createWorld.customize.logarithmic_spiral.style." + this.key);
    }

    public SpiralConfig toConfig(boolean dividerEnabled) {
        double boundaryHalfWidth = dividerEnabled ? 0.5D : 0.0D;
        return new SpiralConfig(
                this.scale,
                this.growth,
                this.spiralRegionWidth,
                this.crossRegionWidth,
                boundaryHalfWidth,
                0,
                0,
                SpiralConfig.DEFAULT_MIN_RADIUS
        );
    }

    public static SpiralPresetStyle fromConfig(SpiralConfig config) {
        for (SpiralPresetStyle style : values()) {
            if (Math.abs(style.scale - config.scale()) < 0.01D
                    && Math.abs(style.growth - config.growth()) < 0.01D) {
                return style;
            }
        }
        return STANDARD;
    }
}
