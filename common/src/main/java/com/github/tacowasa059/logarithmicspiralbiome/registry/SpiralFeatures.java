package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.BoundarySurfaceFeature;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

public class SpiralFeatures {
    public static final Identifier BOUNDARY_SURFACE_ID =
            Identifier.fromNamespaceAndPath(Logarithmicspiralbiome.MODID, "boundary_surface");

    public static final MapCodec<? extends Feature> BOUNDARY_SURFACE = BoundarySurfaceFeature.CODEC;

    private SpiralFeatures() {
    }
}
