package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpiralPresetConfigScreen extends Screen {
    private static final List<Double> SCALE_OPTIONS = Arrays.asList(1.0D, 1.5D, 2.0D, 3.0D, 4.0D, 6.0D, 8.0D, 10.0D, 16.0D);
    private static final List<Double> GROWTH_OPTIONS = Arrays.asList(0.12D, 0.18D, 0.25D, 0.32D, 0.40D, 0.50D, 0.65D);

    private final CreateWorldScreen parent;
    private final WorldCreationContext context;

    private boolean enableOverworld;
    private boolean enableNether;
    private SpiralPresetStyle currentStyle;
    private boolean dividerEnabled;
    private double currentScale;
    private double currentGrowth;

    private CycleButton<SpiralPresetStyle> styleButton;
    private CycleButton<Double> scaleButton;
    private CycleButton<Double> growthButton;

    public SpiralPresetConfigScreen(CreateWorldScreen parent, WorldCreationContext context) {
        super(Component.translatable("createWorld.customize.logarithmic_spiral.title"));
        this.parent = parent;
        this.context = context;

        // デフォルト値はコンフィグから読み込む
        this.enableOverworld = SpiralModConfig.isEnableOverworld();
        this.enableNether = SpiralModConfig.isEnableNether();
        this.currentStyle = SpiralModConfig.getStyle();
        this.dividerEnabled = SpiralModConfig.isDividerBlocks();
        this.currentScale = SpiralModConfig.getScale();
        this.currentGrowth = SpiralModConfig.getGrowth();

        // 既存のディメンション設定があれば初期値を優先同期
        if (context.selectedDimensions().overworld() instanceof NoiseBasedChunkGenerator noiseGen) {
            if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                SpiralConfig config = spiralSource.getConfig();
                this.currentScale = config.scale();
                this.currentGrowth = config.growth();
                this.dividerEnabled = config.boundaryHalfWidth() > 0.0D;
                this.currentStyle = SpiralPresetStyle.fromConfig(config);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int leftCol = centerX - 155;
        int rightCol = centerX + 5;
        int colWidth = 150;
        int rowHeight = 24;
        int startY = this.height / 5;

        // タイトル
        this.addRenderableWidget(new StringWidget(
                centerX - 100,
                startY - 25,
                200,
                20,
                this.title,
                this.font
        ));

        // 行1: オーバーワールド適用 / ネザー適用
        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableOverworld)
                .create(
                        leftCol,
                        startY,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_overworld"),
                        (button, value) -> this.enableOverworld = value
                ));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableNether)
                .create(
                        rightCol,
                        startY,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_nether"),
                        (button, value) -> this.enableNether = value
                ));

        // 行2: 螺旋タイプ / 仕切りブロック
        this.styleButton = this.addRenderableWidget(CycleButton.builder(SpiralPresetStyle::displayName, this.currentStyle)
                .withValues(SpiralPresetStyle.values())
                .create(
                        leftCol,
                        startY + rowHeight,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.style"),
                        (button, value) -> {
                            this.currentStyle = value;
                            if (value != SpiralPresetStyle.CUSTOM) {
                                this.currentScale = findClosest(SCALE_OPTIONS, value.scale());
                                this.currentGrowth = findClosest(GROWTH_OPTIONS, value.growth());
                                if (this.scaleButton != null) this.scaleButton.setValue(this.currentScale);
                                if (this.growthButton != null) this.growthButton.setValue(this.currentGrowth);
                            }
                        }
                ));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.dividerEnabled)
                .create(
                        rightCol,
                        startY + rowHeight,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.divider"),
                        (button, value) -> this.dividerEnabled = value
                ));

        // 行3: スケール直接設定 / 成長率直接設定
        Double initialScale = findClosest(SCALE_OPTIONS, this.currentScale);
        Double initialGrowth = findClosest(GROWTH_OPTIONS, this.currentGrowth);

        this.scaleButton = this.addRenderableWidget(CycleButton.<Double>builder(val -> Component.literal(String.format("%.1f", val)), initialScale)
                .withValues(SCALE_OPTIONS)
                .create(
                        leftCol,
                        startY + rowHeight * 2,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.scale"),
                        (button, value) -> {
                            this.currentScale = value;
                            this.currentStyle = SpiralPresetStyle.CUSTOM;
                            if (this.styleButton != null) this.styleButton.setValue(SpiralPresetStyle.CUSTOM);
                        }
                ));

        this.growthButton = this.addRenderableWidget(CycleButton.<Double>builder(val -> Component.literal(String.format("%.2f", val)), initialGrowth)
                .withValues(GROWTH_OPTIONS)
                .create(
                        rightCol,
                        startY + rowHeight * 2,
                        colWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.growth"),
                        (button, value) -> {
                            this.currentGrowth = value;
                            this.currentStyle = SpiralPresetStyle.CUSTOM;
                            if (this.styleButton != null) this.styleButton.setValue(SpiralPresetStyle.CUSTOM);
                        }
                ));

        // 完了ボタン
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.applyAndClose())
                .bounds(centerX - 155, this.height - 28, 150, 20)
                .build());

        // キャンセルボタン
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                .bounds(centerX + 5, this.height - 28, 150, 20)
                .build());
    }

    private static Double findClosest(List<Double> options, double target) {
        Double best = options.get(0);
        double minDiff = Math.abs(best - target);
        for (Double opt : options) {
            double diff = Math.abs(opt - target);
            if (diff < minDiff) {
                minDiff = diff;
                best = opt;
            }
        }
        return best;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }

    private void applyAndClose() {
        double boundaryHalfWidth = this.dividerEnabled ? 0.5D : 0.0D;
        SpiralConfig newConfig = new SpiralConfig(
                this.currentScale,
                this.currentGrowth,
                this.currentStyle.spiralRegionWidth(),
                this.currentStyle.crossRegionWidth(),
                boundaryHalfWidth,
                0,
                0,
                SpiralConfig.DEFAULT_MIN_RADIUS
        );

        WorldCreationContext.DimensionsUpdater updater = (registryAccess, worldDimensions) -> {
            Map<ResourceKey<LevelStem>, LevelStem> newDimensions = new LinkedHashMap<>(worldDimensions.dimensions());
            HolderGetter<MultiNoiseBiomeSourceParameterList> paramLists =
                    registryAccess.lookupOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : newDimensions.entrySet()) {
                ResourceKey<LevelStem> key = entry.getKey();
                LevelStem stem = entry.getValue();

                if (key.equals(LevelStem.OVERWORLD)) {
                    if (this.enableOverworld) {
                        entry.setValue(updateStemWithSpiral(stem, newConfig, "overworld"));
                    } else {
                        entry.setValue(updateStemWithVanilla(stem, paramLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)));
                    }
                } else if (key.equals(LevelStem.NETHER)) {
                    if (this.enableNether) {
                        entry.setValue(updateStemWithSpiral(stem, newConfig, "nether"));
                    } else {
                        entry.setValue(updateStemWithVanilla(stem, paramLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)));
                    }
                }
            }
            return new WorldDimensions(newDimensions);
        };

        this.parent.getUiState().updateDimensions(updater);
        this.onClose();
    }

    private static LevelStem updateStemWithSpiral(LevelStem stem, SpiralConfig config, String dimension) {
        if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen) {
            if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                SpiralBiomeSource newSource = new SpiralBiomeSource(
                        spiralSource.getBiomes(),
                        spiralSource.getBoundaryBiomeOpt(),
                        config,
                        dimension
                );
                return new LevelStem(stem.type(), new NoiseBasedChunkGenerator(newSource, noiseGen.generatorSettings()));
            }
        }
        return stem;
    }

    private static LevelStem updateStemWithVanilla(LevelStem stem, Holder<MultiNoiseBiomeSourceParameterList> paramList) {
        if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen) {
            MultiNoiseBiomeSource vanillaSource = MultiNoiseBiomeSource.createFromPreset(paramList);
            return new LevelStem(stem.type(), new NoiseBasedChunkGenerator(vanillaSource, noiseGen.generatorSettings()));
        }
        return stem;
    }
}
