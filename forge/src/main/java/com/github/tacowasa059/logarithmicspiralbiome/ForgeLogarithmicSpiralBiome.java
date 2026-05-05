package com.github.tacowasa059.logarithmicspiralbiome;

import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralBiomeSources;
import com.github.tacowasa059.logarithmicspiralbiome.registry.SpiralFeatures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Logarithmicspiralbiome.MODID)
public class ForgeLogarithmicSpiralBiome {
    private static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE.key(), Logarithmicspiralbiome.MODID);

    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, Logarithmicspiralbiome.MODID);

    public ForgeLogarithmicSpiralBiome() {
        BusGroup modBusGroup = FMLJavaModLoadingContext.get().getModBusGroup();
        BIOME_SOURCES.register("logarithmic_spiral", () -> SpiralBiomeSources.LOGARITHMIC_SPIRAL);
        FEATURES.register("boundary_surface", () -> SpiralFeatures.BOUNDARY_SURFACE);
        BIOME_SOURCES.register(modBusGroup);
        FEATURES.register(modBusGroup);
        Logarithmicspiralbiome.init();
    }
}
