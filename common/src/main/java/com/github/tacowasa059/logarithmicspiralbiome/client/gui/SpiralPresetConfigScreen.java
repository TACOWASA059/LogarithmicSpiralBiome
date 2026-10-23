package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature.DividerBlockType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

import java.util.LinkedHashMap;
import java.util.Map;

public class SpiralPresetConfigScreen extends Screen {
    private final CreateWorldScreen parent;
    private final WorldCreationContext context;

    private boolean enableOverworld;
    private boolean enableNether;
    private SpiralPresetStyle currentStyle;
    private boolean dividerEnabled;
    private DividerBlockType currentDividerType;
    private int currentWallHeight;
    private double currentScale;
    private double currentGrowth;

    private CycleButton<SpiralPresetStyle> styleButton;
    private DoubleSlider scaleSlider;
    private DoubleSlider growthSlider;
    private CycleButton<Boolean> dividerButton;
    private CycleButton<DividerBlockType> dividerTypeButton;
    private CycleButton<Integer> wallHeightButton;
    private SpiralPreviewWidget previewWidget;

    public SpiralPresetConfigScreen(CreateWorldScreen parent, WorldCreationContext context) {
        super(Component.translatable("createWorld.customize.logarithmic_spiral.title"));
        this.parent = parent;
        this.context = context;

        // デフォルト値はコンフィグから読み込む
        this.enableOverworld = SpiralModConfig.isEnableOverworld();
        this.enableNether = SpiralModConfig.isEnableNether();
        this.currentStyle = SpiralModConfig.getStyle();
        this.dividerEnabled = SpiralModConfig.isDividerBlocks();
        this.currentDividerType = SpiralModConfig.getDividerType();
        this.currentWallHeight = SpiralModConfig.getWallHeight();
        this.currentScale = SpiralModConfig.getScale();
        this.currentGrowth = SpiralModConfig.getGrowth();

        // 既存のディメンション設定があれば初期値を優先同期
        if (context.selectedDimensions().overworld() instanceof NoiseBasedChunkGenerator noiseGen) {
            if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                SpiralConfig config = spiralSource.getConfig();
                this.currentScale = config.scale();
                this.currentGrowth = config.growth();
                this.dividerEnabled = config.boundaryHalfWidth() > 0.0D;
                this.currentDividerType = config.dividerType();
                this.currentWallHeight = config.wallHeight();
                this.currentStyle = SpiralPresetStyle.fromConfig(config);
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int totalWidth = 340;
        int leftX = centerX - totalWidth / 2;
        int controlsWidth = 180;
        int previewX = leftX + controlsWidth + 16;
        int startY = Math.max(26, this.height / 6 - 8);

        // タイトル
        this.addRenderableWidget(new StringWidget(
                centerX - 120,
                startY - 20,
                240,
                20,
                this.title,
                this.font
        ));

        // --- 左ペイン: コントロール ---
        int rowH = 22;

        // 行1: ディメンション適用切り替え (オーバーワールド / ネザー)
        int dimBtnW = (controlsWidth - 4) / 2;
        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableOverworld)
                .create(
                        leftX,
                        startY,
                        dimBtnW,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_overworld"),
                        (button, value) -> this.enableOverworld = value
                ));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableNether)
                .create(
                        leftX + dimBtnW + 4,
                        startY,
                        dimBtnW,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_nether"),
                        (button, value) -> this.enableNether = value
                ));

        // 行2: 螺旋タイプ（プリセット）
        this.styleButton = this.addRenderableWidget(CycleButton.builder(SpiralPresetStyle::displayName, this.currentStyle)
                .withValues(SpiralPresetStyle.values())
                .create(
                        leftX,
                        startY + rowH,
                        controlsWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.style"),
                        (button, value) -> {
                            this.currentStyle = value;
                            if (value != SpiralPresetStyle.CUSTOM) {
                                this.currentScale = value.scale();
                                this.currentGrowth = value.growth();
                                if (this.scaleSlider != null) this.scaleSlider.setActualValue(this.currentScale);
                                if (this.growthSlider != null) this.growthSlider.setActualValue(this.currentGrowth);
                            }
                            this.refreshPreview();
                        }
                ));

        // 行3: スケールスライダー (Scale: 0.5 〜 16.0, step: 0.1)
        this.scaleSlider = this.addRenderableWidget(new DoubleSlider(
                leftX,
                startY + rowH * 2,
                controlsWidth,
                20,
                Component.translatable("createWorld.customize.logarithmic_spiral.scale"),
                0.5D,
                16.0D,
                0.1D,
                this.currentScale,
                "%.1f",
                val -> {
                    this.currentScale = val;
                    this.markCustom();
                    this.refreshPreview();
                }
        ));

        // 行4: 成長率スライダー (Growth: 0.10 〜 0.70, step: 0.01)
        this.growthSlider = this.addRenderableWidget(new DoubleSlider(
                leftX,
                startY + rowH * 3,
                controlsWidth,
                20,
                Component.translatable("createWorld.customize.logarithmic_spiral.growth"),
                0.10D,
                0.70D,
                0.01D,
                this.currentGrowth,
                "%.2f",
                val -> {
                    this.currentGrowth = val;
                    this.markCustom();
                    this.refreshPreview();
                }
        ));

        // 行5: 仕切りブロック (ON / OFF)
        this.dividerButton = this.addRenderableWidget(CycleButton.onOffBuilder(this.dividerEnabled)
                .create(
                        leftX,
                        startY + rowH * 4,
                        controlsWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.divider"),
                        (button, value) -> {
                            this.dividerEnabled = value;
                            if (this.dividerTypeButton != null) this.dividerTypeButton.active = value;
                            if (this.wallHeightButton != null) this.wallHeightButton.active = value;
                            this.refreshPreview();
                        }
                ));

        // 行6: 仕切りブロックの種類
        this.dividerTypeButton = this.addRenderableWidget(CycleButton.builder(DividerBlockType::displayName, this.currentDividerType)
                .withValues(DividerBlockType.values())
                .create(
                        leftX,
                        startY + rowH * 5,
                        controlsWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.divider_type"),
                        (button, value) -> {
                            this.currentDividerType = value;
                            this.refreshPreview();
                        }
                ));
        this.dividerTypeButton.active = this.dividerEnabled;

        // 行7: 壁の高さ (0: 敷石, 1〜5)
        this.wallHeightButton = this.addRenderableWidget(CycleButton.<Integer>builder(
                        val -> val == 0
                                ? Component.translatable("createWorld.customize.logarithmic_spiral.wall_height.flat")
                                : Component.literal(String.valueOf(val)),
                        this.currentWallHeight)
                .withValues(0, 1, 2, 3, 4, 5)
                .create(
                        leftX,
                        startY + rowH * 6,
                        controlsWidth,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.wall_height"),
                        (button, value) -> this.currentWallHeight = value
                ));
        this.wallHeightButton.active = this.dividerEnabled;

        // --- 右ペイン: リアルタイムマッププレビュー ---
        int previewY = startY + 12;
        this.addRenderableWidget(new StringWidget(
                previewX,
                previewY - 14,
                128,
                12,
                Component.translatable("createWorld.customize.logarithmic_spiral.preview_header"),
                this.font
        ));

        if (this.previewWidget != null) {
            this.previewWidget.close();
        }
        this.previewWidget = this.addRenderableWidget(new SpiralPreviewWidget(
                previewX,
                previewY,
                128
        ));

        this.addRenderableWidget(new StringWidget(
                previewX,
                previewY + 130,
                128,
                10,
                Component.translatable("createWorld.customize.logarithmic_spiral.preview_footer"),
                this.font
        ));

        this.addRenderableWidget(new StringWidget(
                previewX,
                previewY + 142,
                128,
                10,
                Component.translatable("createWorld.customize.logarithmic_spiral.preview_grid_info"),
                this.font
        ));

        // --- 下部: 完了 / キャンセル ---
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.applyAndClose())
                .bounds(centerX - 155, this.height - 28, 150, 20)
                .build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                .bounds(centerX + 5, this.height - 28, 150, 20)
                .build());

        // 初回プレビュー描画
        this.refreshPreview();
    }

    private void markCustom() {
        if (this.currentStyle != SpiralPresetStyle.CUSTOM) {
            this.currentStyle = SpiralPresetStyle.CUSTOM;
            if (this.styleButton != null) {
                this.styleButton.setValue(SpiralPresetStyle.CUSTOM);
            }
        }
    }

    private void refreshPreview() {
        if (this.previewWidget != null) {
            this.previewWidget.updatePreview(this.getCurrentConfig(), this.dividerEnabled);
        }
    }

    private SpiralConfig getCurrentConfig() {
        double boundaryHalfWidth = this.dividerEnabled ? 0.5D : 0.0D;
        return new SpiralConfig(
                this.currentScale,
                this.currentGrowth,
                this.currentStyle.spiralRegionWidth(),
                this.currentStyle.crossRegionWidth(),
                boundaryHalfWidth,
                0,
                0,
                SpiralConfig.DEFAULT_MIN_RADIUS,
                this.currentWallHeight,
                this.currentDividerType
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Modern UI の背景カード（左右のパネルを覆う洗練されたダークグラス風カード）
        int centerX = this.width / 2;
        int totalWidth = 356;
        int cardX = centerX - totalWidth / 2;
        int startY = Math.max(26, this.height / 6 - 8);
        int cardH = 176;

        // 背景カードの角丸調シャドウと半透明ダークグラデーション
        graphics.fill(cardX - 1, startY - 8, cardX + totalWidth + 1, startY + cardH + 1, 0x33FFFFFF);
        graphics.fill(cardX, startY - 7, cardX + totalWidth, startY + cardH, 0xCC10141A);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.previewWidget != null) {
            this.previewWidget.close();
            this.previewWidget = null;
        }
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }

    private void applyAndClose() {
        SpiralConfig newConfig = this.getCurrentConfig();

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
