package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.BoundarySurfaceFeature;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpiralFeatures {
    public static final Identifier BOUNDARY_SURFACE_ID =
            Identifier.fromNamespaceAndPath(Logarithmicspiralbiome.MODID, "boundary_surface");

    public static final Feature<NoneFeatureConfiguration> BOUNDARY_SURFACE = new BoundarySurfaceFeature();

    private SpiralFeatures() {
    }
}
