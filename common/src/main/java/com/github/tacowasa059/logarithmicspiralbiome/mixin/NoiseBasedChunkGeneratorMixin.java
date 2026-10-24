package com.github.tacowasa059.logarithmicspiralbiome.mixin;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin extends ChunkGenerator {

    @Shadow
    @Final
    private Holder<NoiseGeneratorSettings> settings;

    protected NoiseBasedChunkGeneratorMixin() {
        super(null);
    }

    @Inject(method = "doFill", at = @At("RETURN"))
    private void logarithmicspiralbiome$elevateSpawnCenter(
            NoiseChunk noiseChunk,
            ChunkAccess chunkAccess,
            CallbackInfo ci
    ) {
        if (!(this.getBiomeSource() instanceof SpiralBiomeSource spiralSource)) {
            return;
        }
        if (!"overworld".equalsIgnoreCase(spiralSource.getDimension())) {
            return;
        }

        double radius = SpiralModConfig.getSpawnElevateRadius();
        if (radius <= 0.0D) {
            return;
        }

        ChunkPos chunkPos = chunkAccess.getPos();
        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        int maxBlockX = chunkPos.getMaxBlockX();
        int maxBlockZ = chunkPos.getMaxBlockZ();

        double centerX = spiralSource.getConfig().centerX();
        double centerZ = spiralSource.getConfig().centerZ();

        // チャンクが中心円と交差しているか高速判定
        double closestX = Math.max(minBlockX, Math.min(centerX, maxBlockX));
        double closestZ = Math.max(minBlockZ, Math.min(centerZ, maxBlockZ));
        double distSqToChunk = (closestX - centerX) * (closestX - centerX) + (closestZ - centerZ) * (closestZ - centerZ);
        if (distSqToChunk > radius * radius) {
            return;
        }

        BlockState defaultBlock = this.settings.value().defaultBlock();
        Heightmap oceanFloor = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double minTargetHeight = 64.0D;
        double centerExtra = 3.0D;

        for (int localX = 0; localX < 16; localX++) {
            int blockX = minBlockX + localX;
            double dx = blockX - centerX;
            for (int localZ = 0; localZ < 16; localZ++) {
                int blockZ = minBlockZ + localZ;
                double dz = blockZ - centerZ;
                double distSq = dx * dx + dz * dz;
                if (distSq > radius * radius) {
                    continue;
                }

                double r = Math.sqrt(distSq);
                int targetHeight = (int) Math.round(minTargetHeight + centerExtra * Math.cos((Math.PI * r) / (2.0D * radius)));

                int currentOceanFloorY = chunkAccess.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockX, blockZ);
                if (currentOceanFloorY < targetHeight) {
                    int startY = Math.max(chunkAccess.getMinY(), currentOceanFloorY);
                    for (int y = startY; y <= targetHeight; y++) {
                        pos.set(blockX, y, blockZ);
                        BlockState state = chunkAccess.getBlockState(pos);
                        if (!state.isSolid() || state.is(Blocks.WATER)) {
                            int sectionIndex = chunkAccess.getSectionIndex(y);
                            if (sectionIndex >= 0 && sectionIndex < chunkAccess.getSectionsCount()) {
                                net.minecraft.world.level.chunk.LevelChunkSection section = chunkAccess.getSection(sectionIndex);
                                if (section != null) {
                                    section.setBlockState(localX, y & 15, localZ, defaultBlock, false);
                                    oceanFloor.update(localX, y, localZ, defaultBlock);
                                    worldSurface.update(localX, y, localZ, defaultBlock);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
