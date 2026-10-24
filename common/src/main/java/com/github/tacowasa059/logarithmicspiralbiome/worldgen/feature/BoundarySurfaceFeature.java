package com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BoundarySurfaceFeature extends Feature<NoneFeatureConfiguration> {
    private static final int REPLACE_DEPTH = 3;

    public BoundarySurfaceFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        BlockPos origin = context.origin();

        if (!(chunkGenerator.getBiomeSource() instanceof SpiralBiomeSource spiralSource)) {
            return false;
        }

        SpiralConfig config = spiralSource.getConfig();
        boolean isNether = level.dimensionType().hasCeiling();
        BlockState boundaryState = isNether ? config.getNetherBlock().defaultBlockState() : config.getOverworldBlock().defaultBlockState();
        int wallHeight = Math.max(0, config.wallHeight());

        ChunkPos chunkPos = new ChunkPos(origin.getX() >> 4, origin.getZ() >> 4);
        boolean placed = false;
        for (int localZ = 0; localZ < 16; localZ++) {
            int blockZ = chunkPos.getMinBlockZ() + localZ;
            for (int localX = 0; localX < 16; localX++) {
                int blockX = chunkPos.getMinBlockX() + localX;
                if (spiralSource.isBoundary(blockX, blockZ)) {
                    placed |= isNether
                            ? replaceSurfaceNether(level, blockX, blockZ, boundaryState, wallHeight)
                            : replaceSurfaceOverworld(level, blockX, blockZ, boundaryState, wallHeight);
                }
            }
        }
        return placed;
    }

    private static boolean replaceSurfaceOverworld(WorldGenLevel level, int blockX, int blockZ, BlockState boundaryState, int wallHeight) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockX, blockZ) - 1;
        if (surfaceY < level.getMinY() || surfaceY >= level.getMaxY()) {
            return false;
        }

        boolean placed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minY = Math.max(level.getMinY(), surfaceY - REPLACE_DEPTH + 1);
        int maxY = Math.min(level.getMaxY() - 1, surfaceY + wallHeight);
        for (int y = minY; y <= maxY; y++) {
            if (y < level.getMinY()) {
                break;
            }

            pos.set(blockX, y, blockZ);
            BlockState existing = level.getBlockState(pos);
            if (y <= surfaceY && existing.isAir()) {
                continue;
            }

            level.setBlock(pos, boundaryState, 2);
            placed = true;
        }
        return placed;
    }

    private static boolean replaceSurfaceNether(WorldGenLevel level, int blockX, int blockZ, BlockState boundaryState, int wallHeight) {
        boolean placed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int topY = Math.min(120, level.getMaxY() - 8);
        int bottomY = Math.max(level.getMinY() + 5, 31);

        for (int y = topY; y >= bottomY; y--) {
            pos.set(blockX, y, blockZ);
            BlockState current = level.getBlockState(pos);

            if (!current.isAir() && !current.is(Blocks.BEDROCK) && !current.is(Blocks.LAVA)) {
                pos.set(blockX, y + 1, blockZ);
                BlockState above = level.getBlockState(pos);

                if (above.isAir() || above.is(Blocks.FIRE) || above.is(Blocks.SOUL_FIRE)) {
                    int surfaceY = y;
                    int minY = Math.max(level.getMinY(), surfaceY - REPLACE_DEPTH + 1);
                    int maxY = Math.min(level.getMaxY() - 1, surfaceY + wallHeight);

                    for (int wallY = minY; wallY <= maxY; wallY++) {
                        pos.set(blockX, wallY, blockZ);
                        BlockState existing = level.getBlockState(pos);
                        if (wallY > surfaceY && !existing.isAir() && !existing.is(Blocks.FIRE) && !existing.is(Blocks.SOUL_FIRE)) {
                            continue;
                        }
                        if (wallY <= surfaceY && existing.isAir()) {
                            continue;
                        }
                        level.setBlock(pos, boundaryState, 2);
                        placed = true;
                    }
                    y -= REPLACE_DEPTH;
                }
            }
        }
        return placed;
    }
}
