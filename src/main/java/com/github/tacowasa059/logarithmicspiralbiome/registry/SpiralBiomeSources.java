package com.github.tacowasa059.logarithmicspiralbiome.registry;

import com.github.tacowasa059.logarithmicspiralbiome.Logarithmicspiralbiome;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SpiralBiomeSources {
    private static final DeferredRegister<Codec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE.key(), Logarithmicspiralbiome.MODID);

    public static final RegistryObject<Codec<SpiralBiomeSource>> LOGARITHMIC_SPIRAL =
            BIOME_SOURCES.register("logarithmic_spiral", () -> SpiralBiomeSource.CODEC);

    public static void register(IEventBus eventBus) {
        BIOME_SOURCES.register(eventBus);
    }
}
