package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

public class SpiralBiomeSources {
    public static final ResourceLocation LOGARITHMIC_SPIRAL_ID =
            new ResourceLocation(Logarithmicspiralbiome.MODID, "logarithmic_spiral");

    public static final Codec<? extends BiomeSource> LOGARITHMIC_SPIRAL = SpiralBiomeSource.CODEC;

    private SpiralBiomeSources() {
    }
}
