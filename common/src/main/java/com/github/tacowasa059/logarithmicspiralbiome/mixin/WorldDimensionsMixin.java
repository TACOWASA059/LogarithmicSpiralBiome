package com.github.tacowasa059.logarithmicspiralbiome.mixin;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(WorldDimensions.class)
public abstract class WorldDimensionsMixin {
    @Shadow
    @Final
    private Map<ResourceKey<LevelStem>, LevelStem> dimensions;

    @Shadow
    private static Lifecycle checkStability(ResourceKey<LevelStem> key, LevelStem dimension) {
        throw new AssertionError();
    }

    @Inject(method = "bake", at = @At("RETURN"), cancellable = true)
    private void logarithmicspiralbiome$wrapSpiralDimensions(
            Registry<LevelStem> baseDimensions,
            CallbackInfoReturnable<WorldDimensions.Complete> cir
    ) {
        WorldDimensions.Complete original = cir.getReturnValue();
        Registry<LevelStem> currentDimensions = original.dimensions();

        // 1. このワールドが Logarithmic Spiral プリセットで生成されているか判定
        LevelStem presetOverworld = this.dimensions.get(LevelStem.OVERWORLD);
        LevelStem currentOverworld = currentDimensions.get(LevelStem.OVERWORLD).map(Holder::value).orElse(null);

        boolean isSpiralOverworld = (presetOverworld != null && presetOverworld.generator().getBiomeSource() instanceof SpiralBiomeSource)
                || (currentOverworld != null && currentOverworld.generator().getBiomeSource() instanceof SpiralBiomeSource);

        if (!isSpiralOverworld) {
            return;
        }

        boolean changed = false;
        List<LevelStemEntry> newEntries = new ArrayList<>();

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : currentDimensions.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();

            if (key.equals(LevelStem.NETHER) && SpiralModConfig.isEnableNether()) {
                if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen
                        && !(noiseGen.getBiomeSource() instanceof SpiralBiomeSource)) {
                    LevelStem spiralNether = convertToSpiralNether(stem, noiseGen);
                    newEntries.add(new LevelStemEntry(key, spiralNether));
                    changed = true;
                    continue;
                }
            } else if (key.equals(LevelStem.OVERWORLD) && SpiralModConfig.isEnableOverworld()) {
                if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen
                        && !(noiseGen.getBiomeSource() instanceof SpiralBiomeSource)) {
                    LevelStem spiralOverworld = convertToSpiralOverworld(stem, noiseGen);
                    newEntries.add(new LevelStemEntry(key, spiralOverworld));
                    changed = true;
                    continue;
                }
            }

            newEntries.add(new LevelStemEntry(key, stem));
        }

        if (changed) {
            Lifecycle lifecycle = currentDimensions.registryLifecycle();
            WritableRegistry<LevelStem> writableRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, lifecycle);
            for (LevelStemEntry entry : newEntries) {
                writableRegistry.register(
                        entry.key(),
                        entry.value(),
                        new RegistrationInfo(Optional.empty(), checkStability(entry.key(), entry.value()))
                );
            }
            Registry<LevelStem> bakedRegistry = writableRegistry.freeze();
            cir.setReturnValue(new WorldDimensions.Complete(bakedRegistry, original.specialWorldProperty()));
        }
    }

    private LevelStem convertToSpiralNether(LevelStem stem, NoiseBasedChunkGenerator noiseGen) {
        List<Holder<Biome>> biomesList = noiseGen.getBiomeSource().possibleBiomes().stream().toList();
        HolderSet<Biome> biomes = HolderSet.direct(biomesList);

        Optional<Holder<Biome>> boundaryBiomeOpt = Optional.empty();
        LevelStem presetNether = this.dimensions.get(LevelStem.NETHER);
        if (presetNether != null && presetNether.generator().getBiomeSource() instanceof SpiralBiomeSource spiralPreset) {
            boundaryBiomeOpt = spiralPreset.getBoundaryBiomeOpt();
        }

        SpiralConfig config = createCurrentConfig();
        SpiralBiomeSource newSource = new SpiralBiomeSource(biomes, boundaryBiomeOpt, config, "nether");
        return new LevelStem(stem.type(), new NoiseBasedChunkGenerator(newSource, noiseGen.generatorSettings()));
    }

    private LevelStem convertToSpiralOverworld(LevelStem stem, NoiseBasedChunkGenerator noiseGen) {
        List<Holder<Biome>> biomesList = noiseGen.getBiomeSource().possibleBiomes().stream().toList();
        HolderSet<Biome> biomes = HolderSet.direct(biomesList);

        Optional<Holder<Biome>> boundaryBiomeOpt = Optional.empty();
        LevelStem presetOverworld = this.dimensions.get(LevelStem.OVERWORLD);
        if (presetOverworld != null && presetOverworld.generator().getBiomeSource() instanceof SpiralBiomeSource spiralPreset) {
            boundaryBiomeOpt = spiralPreset.getBoundaryBiomeOpt();
        }

        SpiralConfig config = createCurrentConfig();
        SpiralBiomeSource newSource = new SpiralBiomeSource(biomes, boundaryBiomeOpt, config, "overworld");
        return new LevelStem(stem.type(), new NoiseBasedChunkGenerator(newSource, noiseGen.generatorSettings()));
    }

    private static SpiralConfig createCurrentConfig() {
        double boundaryHalfWidth = SpiralModConfig.isDividerBlocks() ? 0.5D : 0.0D;
        return new SpiralConfig(
                SpiralModConfig.getScale(),
                SpiralModConfig.getGrowth(),
                SpiralModConfig.getStyle().spiralRegionWidth(),
                SpiralModConfig.getStyle().crossRegionWidth(),
                boundaryHalfWidth,
                0,
                0,
                SpiralConfig.DEFAULT_MIN_RADIUS,
                SpiralModConfig.getWallHeight(),
                SpiralModConfig.getOverworldDividerBlock(),
                SpiralModConfig.getNetherDividerBlock()
        );
    }

    private record LevelStemEntry(ResourceKey<LevelStem> key, LevelStem value) {}
}
