package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.BiomeSource;

public class SpiralBiomeSources {
    public static final Identifier LOGARITHMIC_SPIRAL_ID =
            Identifier.fromNamespaceAndPath(Logarithmicspiralbiome.MODID, "logarithmic_spiral");

    public static final MapCodec<? extends BiomeSource> LOGARITHMIC_SPIRAL = SpiralBiomeSource.CODEC;

    private SpiralBiomeSources() {
    }
}
