package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

public class SpiralCalculator {
    private static final double ORIGIN_OFFSET = 0.5D;

    private final SpiralConfig config;
    private final double spiralPhaseToBlockDistance;
    private final double crossPhaseToBlockDistance;

    public SpiralCalculator(SpiralConfig config) {
        this.config = config;
        this.spiralPhaseToBlockDistance = 1.0D / Math.sqrt(1.0D + 1.0D / (config.growth() * config.growth()));
        this.crossPhaseToBlockDistance = 1.0D / Math.sqrt(1.0D + config.growth() * config.growth());
    }

    public SpiralConfig config() {
        return config;
    }

    public int regionIndex(int blockX, int blockZ, int regionCount) {
        int spiralIndex = (int) Math.floor(spiralPhase(blockX, blockZ) / config.spiralRegionWidth());
        int crossIndex = (int) Math.floor(crossPhase(blockX, blockZ) / config.crossRegionWidth());
        return Math.floorMod(spiralIndex * 31 + crossIndex, regionCount);
    }

    public boolean isBoundary(int blockX, int blockZ) {
        if (config.boundaryHalfWidth() <= 0.0D) {
            return false;
        }
        double radius = effectiveRadius(blockX, blockZ);
        double scaledWallHalfWidth = config.boundaryHalfWidth() / config.scale();
        double spiralDistance = distanceToPhaseBoundary(
                spiralPhase(blockX, blockZ),
                config.spiralRegionWidth(),
                radius,
                spiralPhaseToBlockDistance
        );
        double crossDistance = distanceToPhaseBoundary(
                crossPhase(blockX, blockZ),
                config.crossRegionWidth(),
                radius,
                crossPhaseToBlockDistance
        );
        return Math.min(spiralDistance, crossDistance) <= scaledWallHalfWidth;
    }

    private double spiralPhase(int blockX, int blockZ) {
        double x = scaledCoordinate(blockX - config.centerX());
        double z = scaledCoordinate(blockZ - config.centerZ());
        double radius = effectiveRadius(x, z);
        double theta = Math.atan2(z, x);
        return theta - Math.log(radius) / config.growth();
    }

    private double crossPhase(int blockX, int blockZ) {
        double x = scaledCoordinate(blockX - config.centerX());
        double z = scaledCoordinate(blockZ - config.centerZ());
        double radius = effectiveRadius(x, z);
        double theta = Math.atan2(z, x);
        return theta + config.growth() * Math.log(radius);
    }

    private static double distanceToPhaseBoundary(double phase, double phaseWidth, double radius, double phaseToBlockDistance) {
        double normalized = positiveModulo(phase, phaseWidth);
        double phaseDistance = Math.min(normalized, phaseWidth - normalized);
        return phaseDistance * radius * phaseToBlockDistance;
    }

    private double effectiveRadius(int blockX, int blockZ) {
        return effectiveRadius(scaledCoordinate(blockX - config.centerX()), scaledCoordinate(blockZ - config.centerZ()));
    }

    private double effectiveRadius(double x, double z) {
        return Math.max(config.minRadius(), Math.hypot(x, z));
    }

    private double scaledCoordinate(int coord) {
        return (coord + ORIGIN_OFFSET) / config.scale();
    }

    private static double positiveModulo(double value, double modulo) {
        double result = value % modulo;
        return result < 0.0D ? result + modulo : result;
    }
}
