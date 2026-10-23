package com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralMath;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public record BoundarySurfaceFeature() implements Feature {
    public static final MapCodec<BoundarySurfaceFeature> CODEC = MapCodec.unit(BoundarySurfaceFeature::new);

    private static final double BOUNDARY_HALF_WIDTH_BLOCKS = 0.5D;
    private static final int REPLACE_DEPTH = 3;
    private static final int WALL_HEIGHT_ABOVE_SURFACE = 3;
    private static final BlockState OVERWORLD_BOUNDARY_STATE = Blocks.STONE.defaultBlockState();
    private static final BlockState NETHER_BOUNDARY_STATE = Blocks.BLACKSTONE.defaultBlockState();

    @Override
    public MapCodec<BoundarySurfaceFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        // 仕切りブロックの配置は景観保護のため無効化
        return false;
    }

    private static boolean replaceSurfaceOverworld(WorldGenLevel level, int blockX, int blockZ) {
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

            level.setBlock(pos, OVERWORLD_BOUNDARY_STATE, 2);
            placed = true;
        }
        return placed;
    }

    private static boolean replaceSurfaceNether(WorldGenLevel level, int blockX, int blockZ) {
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
                    int maxY = Math.min(level.getMaxY() - 1, surfaceY + WALL_HEIGHT_ABOVE_SURFACE);

                    for (int wallY = minY; wallY <= maxY; wallY++) {
                        pos.set(blockX, wallY, blockZ);
                        BlockState existing = level.getBlockState(pos);
                        if (wallY > surfaceY && !existing.isAir() && !existing.is(Blocks.FIRE) && !existing.is(Blocks.SOUL_FIRE)) {
                            continue;
                        }
                        if (wallY <= surfaceY && existing.isAir()) {
                            continue;
                        }
                        level.setBlock(pos, NETHER_BOUNDARY_STATE, 2);
                        placed = true;
                    }
                    y -= REPLACE_DEPTH;
                }
            }
        }
        return placed;
    }
}
