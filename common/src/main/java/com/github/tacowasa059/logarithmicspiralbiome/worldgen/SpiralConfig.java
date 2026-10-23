package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.DividerBlockType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record SpiralConfig(
        double scale,
        double growth,
        double spiralRegionWidth,
        double crossRegionWidth,
        double boundaryHalfWidth,
        int centerX,
        int centerZ,
        double minRadius,
        int wallHeight,
        DividerBlockType dividerType
) {
    public static final double DEFAULT_SCALE = 3.0D;
    public static final double DEFAULT_GROWTH = 0.32D;
    public static final double DEFAULT_SPIRAL_REGION_WIDTH = Math.PI / 8.0D;
    public static final double DEFAULT_CROSS_REGION_WIDTH = Math.PI / 5.0D;
    public static final double DEFAULT_BOUNDARY_HALF_WIDTH = 0.0D;
    public static final int DEFAULT_CENTER_X = 0;
    public static final int DEFAULT_CENTER_Z = 0;
    public static final double DEFAULT_MIN_RADIUS = 16.0D;
    public static final int DEFAULT_WALL_HEIGHT = 2;
    public static final DividerBlockType DEFAULT_DIVIDER_TYPE = DividerBlockType.STONE;

    public static final SpiralConfig DEFAULT = new SpiralConfig(
            DEFAULT_SCALE,
            DEFAULT_GROWTH,
            DEFAULT_SPIRAL_REGION_WIDTH,
            DEFAULT_CROSS_REGION_WIDTH,
            DEFAULT_BOUNDARY_HALF_WIDTH,
            DEFAULT_CENTER_X,
            DEFAULT_CENTER_Z,
            DEFAULT_MIN_RADIUS,
            DEFAULT_WALL_HEIGHT,
            DEFAULT_DIVIDER_TYPE
    );

    public SpiralConfig(double scale, double growth, double spiralRegionWidth, double crossRegionWidth, double boundaryHalfWidth, int centerX, int centerZ, double minRadius) {
        this(scale, growth, spiralRegionWidth, crossRegionWidth, boundaryHalfWidth, centerX, centerZ, minRadius, DEFAULT_WALL_HEIGHT, DEFAULT_DIVIDER_TYPE);
    }

    public static final MapCodec<SpiralConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("scale").forGetter(config -> Optional.of(config.scale())),
            Codec.DOUBLE.optionalFieldOf("growth").forGetter(config -> Optional.of(config.growth())),
            Codec.DOUBLE.optionalFieldOf("spiral_region_width").forGetter(config -> Optional.of(config.spiralRegionWidth())),
            Codec.DOUBLE.optionalFieldOf("cross_region_width").forGetter(config -> Optional.of(config.crossRegionWidth())),
            Codec.DOUBLE.optionalFieldOf("boundary_half_width").forGetter(config -> Optional.of(config.boundaryHalfWidth())),
            Codec.INT.optionalFieldOf("center_x").forGetter(config -> Optional.of(config.centerX())),
            Codec.INT.optionalFieldOf("center_z").forGetter(config -> Optional.of(config.centerZ())),
            Codec.DOUBLE.optionalFieldOf("min_radius").forGetter(config -> Optional.of(config.minRadius())),
            Codec.INT.optionalFieldOf("wall_height").forGetter(config -> Optional.of(config.wallHeight())),
            Codec.STRING.optionalFieldOf("divider_type").forGetter(config -> Optional.of(config.dividerType().getKey()))
    ).apply(instance, (scaleOpt, growthOpt, spiralOpt, crossOpt, boundaryOpt, centerXOpt, centerZOpt, minRadiusOpt, wallHeightOpt, dividerTypeOpt) -> {
        SpiralConfig fallback = SpiralModConfig.getEffectiveConfig();
        return new SpiralConfig(
                scaleOpt.orElse(fallback.scale()),
                growthOpt.orElse(fallback.growth()),
                spiralOpt.orElse(fallback.spiralRegionWidth()),
                crossOpt.orElse(fallback.crossRegionWidth()),
                boundaryOpt.orElse(fallback.boundaryHalfWidth()),
                centerXOpt.orElse(fallback.centerX()),
                centerZOpt.orElse(fallback.centerZ()),
                minRadiusOpt.orElse(fallback.minRadius()),
                wallHeightOpt.orElse(fallback.wallHeight()),
                dividerTypeOpt.map(DividerBlockType::byKey).orElse(fallback.dividerType())
        );
    }));
}
