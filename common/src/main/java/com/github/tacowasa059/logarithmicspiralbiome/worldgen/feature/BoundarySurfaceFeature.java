package com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralMath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BoundarySurfaceFeature extends Feature<NoneFeatureConfiguration> {
    private static final double BOUNDARY_HALF_WIDTH_BLOCKS = 0.5D;
    private static final int REPLACE_DEPTH = 3;
    private static final int WALL_HEIGHT_ABOVE_SURFACE = 3;
    private static final BlockState BOUNDARY_STATE = Blocks.STONE.defaultBlockState();

    public BoundarySurfaceFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkPos chunkPos = new ChunkPos(context.origin());
        boolean placed = false;
        for (int localZ = 0; localZ < 16; localZ++) {
            int blockZ = chunkPos.getMinBlockZ() + localZ;
            for (int localX = 0; localX < 16; localX++) {
                int blockX = chunkPos.getMinBlockX() + localX;
                if (SpiralMath.isBoundary(blockX, blockZ, BOUNDARY_HALF_WIDTH_BLOCKS)) {
                    placed |= replaceSurface(level, blockX, blockZ);
                }
            }
        }
        return placed;
    }

    private static boolean replaceSurface(WorldGenLevel level, int blockX, int blockZ) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockX, blockZ) - 1;
        if (surfaceY < level.getMinY() || surfaceY >= level.getMaxY()) {
            return false;
        }

        boolean placed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minY = Math.max(level.getMinY(), surfaceY - REPLACE_DEPTH + 1);
        int maxY = Math.min(level.getMaxY() - 1, surfaceY + WALL_HEIGHT_ABOVE_SURFACE);
        for (int y = minY; y <= maxY; y++) {
            if (y < level.getMinY()) {
                break;
            }

            pos.set(blockX, y, blockZ);
            BlockState existing = level.getBlockState(pos);
            if (y <= surfaceY && existing.isAir()) {
                continue;
            }

            level.setBlock(pos, BOUNDARY_STATE, 2);
            placed = true;
        }
        return placed;
    }
}
