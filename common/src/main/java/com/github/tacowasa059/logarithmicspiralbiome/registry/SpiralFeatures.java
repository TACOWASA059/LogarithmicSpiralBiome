package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.BoundarySurfaceFeature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpiralFeatures {
    public static final ResourceLocation BOUNDARY_SURFACE_ID =
            new ResourceLocation(Logarithmicspiralbiome.MODID, "boundary_surface");

    public static final Feature<NoneFeatureConfiguration> BOUNDARY_SURFACE = new BoundarySurfaceFeature();

    private SpiralFeatures() {
    }
}
