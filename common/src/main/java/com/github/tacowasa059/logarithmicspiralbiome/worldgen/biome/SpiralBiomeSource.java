package com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import com.google.common.base.Suppliers;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SpiralBiomeSource extends BiomeSource implements BiomeResolver {
    public static final MapCodec<SpiralBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(source -> source.biomes),
            Biome.CODEC.fieldOf("boundary_biome").forGetter(source -> source.boundaryBiome)
    ).apply(instance, SpiralBiomeSource::new));

    private static final double BOUNDARY_HALF_WIDTH_BLOCKS = 0.5D;

    private final HolderSet<Biome> biomes;
    private final Holder<Biome> boundaryBiome;
    private final Supplier<List<Holder<Biome>>> sortedBiomes;

    public SpiralBiomeSource(HolderSet<Biome> biomes, Holder<Biome> boundaryBiome) {
        this.biomes = biomes;
        this.boundaryBiome = boundaryBiome;
        this.sortedBiomes = Suppliers.memoize(() -> biomes.stream()
                .filter(biome -> !biome.equals(boundaryBiome))
                .sorted(Comparator.comparing(SpiralBiomeSource::sortKey))
                .toList());
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return SpiralBiomeSources.LOGARITHMIC_SPIRAL;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(this.biomes.stream(), Stream.of(boundaryBiome));
    }

    @Override
    public BiomeResolver createResolver(Climate.Sampler sampler) {
        return this;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
        List<Holder<Biome>> biomesList = this.sortedBiomes.get();
        if (biomesList.isEmpty()) {
            throw new IllegalStateException("Spiral biome source requires at least one biome");
        }

        int blockX = QuartPos.toBlock(quartX);
        int blockZ = QuartPos.toBlock(quartZ);
        if (SpiralMath.isBoundary(blockX, blockZ, BOUNDARY_HALF_WIDTH_BLOCKS)) {
            return boundaryBiome;
        }
        return biomesList.get(SpiralMath.regionIndex(blockX, blockZ, biomesList.size()));
    }

    private static String sortKey(Holder<Biome> biome) {
        return biome.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse("");
    }
}
