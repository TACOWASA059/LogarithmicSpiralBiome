package com.github.tacowasa059.logarithmicspiralbiome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralFeatures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Logarithmicspiralbiome.MODID)
public class NeoForgeLogarithmicSpiralBiome {
    private static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE.key(), Logarithmicspiralbiome.MODID);

    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE.key(), Logarithmicspiralbiome.MODID);

    public NeoForgeLogarithmicSpiralBiome(IEventBus eventBus) {
        BIOME_SOURCES.register("logarithmic_spiral", () -> SpiralBiomeSources.LOGARITHMIC_SPIRAL);
        FEATURES.register("boundary_surface", () -> SpiralFeatures.BOUNDARY_SURFACE);
        BIOME_SOURCES.register(eventBus);
        FEATURES.register(eventBus);
        Logarithmicspiralbiome.init();
    }
}
