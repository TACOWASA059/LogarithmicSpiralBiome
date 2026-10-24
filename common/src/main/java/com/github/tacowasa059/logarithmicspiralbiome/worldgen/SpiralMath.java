package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

public final class SpiralMath {
    private static final SpiralCalculator DEFAULT_CALCULATOR = new SpiralCalculator(SpiralConfig.DEFAULT);

    private SpiralMath() {
    }

    public static int regionIndex(int blockX, int blockZ, int regionCount) {
        return DEFAULT_CALCULATOR.regionIndex(blockX, blockZ, regionCount);
    }

    public static boolean isBoundary(int blockX, int blockZ, double wallHalfWidthBlocks) {
        return DEFAULT_CALCULATOR.isBoundary(blockX, blockZ);
    }
}
