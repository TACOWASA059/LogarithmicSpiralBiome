package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SpiralConfig(
        double scale,
        double growth,
        double spiralRegionWidth,
        double crossRegionWidth,
        double boundaryHalfWidth,
        int centerX,
        int centerZ,
        double minRadius
) {
    public static final double DEFAULT_SCALE = 3.0D;
    public static final double DEFAULT_GROWTH = 0.32D;
    public static final double DEFAULT_SPIRAL_REGION_WIDTH = Math.PI / 8.0D;
    public static final double DEFAULT_CROSS_REGION_WIDTH = Math.PI / 5.0D;
    public static final double DEFAULT_BOUNDARY_HALF_WIDTH = 0.0D;
    public static final int DEFAULT_CENTER_X = 0;
    public static final int DEFAULT_CENTER_Z = 0;
    public static final double DEFAULT_MIN_RADIUS = 16.0D;

    public static final SpiralConfig DEFAULT = new SpiralConfig(
            DEFAULT_SCALE,
            DEFAULT_GROWTH,
            DEFAULT_SPIRAL_REGION_WIDTH,
            DEFAULT_CROSS_REGION_WIDTH,
            DEFAULT_BOUNDARY_HALF_WIDTH,
            DEFAULT_CENTER_X,
            DEFAULT_CENTER_Z,
            DEFAULT_MIN_RADIUS
    );

    public static final MapCodec<SpiralConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("scale", DEFAULT_SCALE).forGetter(SpiralConfig::scale),
            Codec.DOUBLE.optionalFieldOf("growth", DEFAULT_GROWTH).forGetter(SpiralConfig::growth),
            Codec.DOUBLE.optionalFieldOf("spiral_region_width", DEFAULT_SPIRAL_REGION_WIDTH).forGetter(SpiralConfig::spiralRegionWidth),
            Codec.DOUBLE.optionalFieldOf("cross_region_width", DEFAULT_CROSS_REGION_WIDTH).forGetter(SpiralConfig::crossRegionWidth),
            Codec.DOUBLE.optionalFieldOf("boundary_half_width", DEFAULT_BOUNDARY_HALF_WIDTH).forGetter(SpiralConfig::boundaryHalfWidth),
            Codec.INT.optionalFieldOf("center_x", DEFAULT_CENTER_X).forGetter(SpiralConfig::centerX),
            Codec.INT.optionalFieldOf("center_z", DEFAULT_CENTER_Z).forGetter(SpiralConfig::centerZ),
            Codec.DOUBLE.optionalFieldOf("min_radius", DEFAULT_MIN_RADIUS).forGetter(SpiralConfig::minRadius)
    ).apply(instance, SpiralConfig::new));
}
