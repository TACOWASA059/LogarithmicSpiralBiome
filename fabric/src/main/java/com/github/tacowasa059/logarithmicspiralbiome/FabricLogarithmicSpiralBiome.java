package com.github.tacowasa059.logarithmicspiralbiome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralFeatures;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FabricLogarithmicSpiralBiome implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.BIOME_SOURCE, SpiralBiomeSources.LOGARITHMIC_SPIRAL_ID, SpiralBiomeSources.LOGARITHMIC_SPIRAL);
        Registry.register(BuiltInRegistries.FEATURE, SpiralFeatures.BOUNDARY_SURFACE_ID, SpiralFeatures.BOUNDARY_SURFACE);
        Logarithmicspiralbiome.init();
    }
}
