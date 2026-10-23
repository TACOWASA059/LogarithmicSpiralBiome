package com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralCalculator;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SpiralBiomeSource extends BiomeSource implements BiomeResolver {
    public static final MapCodec<SpiralBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(SpiralBiomeSource::getBiomes),
            Biome.CODEC.optionalFieldOf("boundary_biome").forGetter(SpiralBiomeSource::getBoundaryBiomeOpt),
            SpiralConfig.MAP_CODEC.forGetter(SpiralBiomeSource::getConfig)
    ).apply(instance, SpiralBiomeSource::new));

    private final HolderSet<Biome> biomes;
    private final Optional<Holder<Biome>> boundaryBiome;
    private final SpiralConfig config;
    private final SpiralCalculator calculator;
    private final Supplier<List<Holder<Biome>>> sortedBiomes;

    public SpiralBiomeSource(HolderSet<Biome> biomes, Optional<Holder<Biome>> boundaryBiome, SpiralConfig config) {
        this.biomes = biomes;
        this.boundaryBiome = boundaryBiome;
        this.config = config != null ? config : SpiralConfig.DEFAULT;
        this.calculator = new SpiralCalculator(this.config);
        this.sortedBiomes = Suppliers.memoize(() -> biomes.stream()
                .filter(biome -> boundaryBiome.isEmpty() || !biome.equals(boundaryBiome.get()))
                .sorted(Comparator.comparing(SpiralBiomeSource::sortKey))
                .toList());
    }

    public SpiralBiomeSource(HolderSet<Biome> biomes, Holder<Biome> boundaryBiome, SpiralConfig config) {
        this(biomes, Optional.ofNullable(boundaryBiome), config);
    }

    public SpiralBiomeSource(HolderSet<Biome> biomes, Holder<Biome> boundaryBiome) {
        this(biomes, Optional.ofNullable(boundaryBiome), SpiralConfig.DEFAULT);
    }

    public HolderSet<Biome> getBiomes() {
        return this.biomes;
    }

    public Optional<Holder<Biome>> getBoundaryBiomeOpt() {
        return this.boundaryBiome;
    }

    public Holder<Biome> getBoundaryBiome() {
        return this.boundaryBiome.orElse(null);
    }

    public SpiralConfig getConfig() {
        return this.config;
    }

    public boolean isBoundary(int blockX, int blockZ) {
        return this.boundaryBiome.isPresent() && this.calculator.isBoundary(blockX, blockZ);
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return SpiralBiomeSources.LOGARITHMIC_SPIRAL;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.boundaryBiome
                .map(holder -> Stream.concat(this.biomes.stream(), Stream.of(holder)))
                .orElseGet(this.biomes::stream);
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
        if (this.calculator.isBoundary(blockX, blockZ) && this.boundaryBiome.isPresent()) {
            return this.boundaryBiome.get();
        }
        return biomesList.get(this.calculator.regionIndex(blockX, blockZ, biomesList.size()));
    }

    private static String sortKey(Holder<Biome> biome) {
        return biome.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse("");
    }
}
