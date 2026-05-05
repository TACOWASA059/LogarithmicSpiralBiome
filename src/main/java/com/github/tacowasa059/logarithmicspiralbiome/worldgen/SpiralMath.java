package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

public final class SpiralMath {
    private static final double ORIGIN_OFFSET = 0.5D;
    private static final double GROWTH = 0.32D;
    private static final double RADIUS_SCALE = 3.0D;
    private static final double SPIRAL_REGION_WIDTH = Math.PI / 8.0D;
    private static final double CROSS_REGION_WIDTH = Math.PI / 5.0D;
    private static final double MIN_EFFECTIVE_RADIUS = 16.0D;
    private static final double SPIRAL_PHASE_TO_BLOCK_DISTANCE = 1.0D / Math.sqrt(1.0D + 1.0D / (GROWTH * GROWTH));
    private static final double CROSS_PHASE_TO_BLOCK_DISTANCE = 1.0D / Math.sqrt(1.0D + GROWTH * GROWTH);

    private SpiralMath() {
    }

    public static int regionIndex(int blockX, int blockZ, int regionCount) {
        int spiralIndex = (int) Math.floor(spiralPhase(blockX, blockZ) / SPIRAL_REGION_WIDTH);
        int crossIndex = (int) Math.floor(crossPhase(blockX, blockZ) / CROSS_REGION_WIDTH);
        return Math.floorMod(spiralIndex * 31 + crossIndex, regionCount);
    }

    public static boolean isBoundary(int blockX, int blockZ, double wallHalfWidthBlocks) {
        double radius = effectiveRadius(blockX, blockZ);
        double scaledWallHalfWidth = wallHalfWidthBlocks / RADIUS_SCALE;
        double spiralDistance = distanceToPhaseBoundary(
                spiralPhase(blockX, blockZ),
                SPIRAL_REGION_WIDTH,
                radius,
                SPIRAL_PHASE_TO_BLOCK_DISTANCE
        );
        double crossDistance = distanceToPhaseBoundary(
                crossPhase(blockX, blockZ),
                CROSS_REGION_WIDTH,
                radius,
                CROSS_PHASE_TO_BLOCK_DISTANCE
        );
        return Math.min(spiralDistance, crossDistance) <= scaledWallHalfWidth;
    }

    private static double spiralPhase(int blockX, int blockZ) {
        double x = scaledCoordinate(blockX);
        double z = scaledCoordinate(blockZ);
        double radius = effectiveRadius(x, z);
        double theta = Math.atan2(z, x);
        return theta - Math.log(radius) / GROWTH;
    }

    private static double crossPhase(int blockX, int blockZ) {
        double x = scaledCoordinate(blockX);
        double z = scaledCoordinate(blockZ);
        double radius = effectiveRadius(x, z);
        double theta = Math.atan2(z, x);
        return theta + GROWTH * Math.log(radius);
    }

    private static double distanceToPhaseBoundary(double phase, double phaseWidth, double radius, double phaseToBlockDistance) {
        double normalized = positiveModulo(phase, phaseWidth);
        double phaseDistance = Math.min(normalized, phaseWidth - normalized);
        return phaseDistance * radius * phaseToBlockDistance;
    }

    private static double effectiveRadius(int blockX, int blockZ) {
        return effectiveRadius(scaledCoordinate(blockX), scaledCoordinate(blockZ));
    }

    private static double effectiveRadius(double x, double z) {
        return Math.max(MIN_EFFECTIVE_RADIUS, Math.hypot(x, z));
    }

    private static double scaledCoordinate(int blockCoordinate) {
        return (blockCoordinate + ORIGIN_OFFSET) / RADIUS_SCALE;
    }

    private static double positiveModulo(double value, double modulo) {
        double result = value % modulo;
        return result < 0.0D ? result + modulo : result;
    }
}
