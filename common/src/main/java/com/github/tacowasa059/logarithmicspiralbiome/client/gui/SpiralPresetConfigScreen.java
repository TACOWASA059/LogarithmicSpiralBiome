package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.config.SpiralModConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SpiralPresetConfigScreen extends Screen {
    private final CreateWorldScreen parent;
    private final WorldCreationContext context;

    private boolean enableOverworld;
    private boolean enableNether;
    private SpiralPresetStyle currentStyle;
    private boolean dividerEnabled;
    private Block overworldBlock;
    private Block netherBlock;
    private int currentWallHeight;
    private double currentScale;
    private double currentGrowth;
    private String previewDimension = "overworld";
    private CycleButton<String> previewDimensionButton;

    private CycleButton<SpiralPresetStyle> styleButton;
    private DoubleSlider scaleSlider;
    private DoubleSlider growthSlider;
    private CycleButton<Boolean> dividerButton;
    private Button overworldBlockButton;
    private Button netherBlockButton;
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
        this.overworldBlock = SpiralConfig.resolveBlock(SpiralModConfig.getOverworldDividerBlock(), Blocks.STONE);
        this.netherBlock = SpiralConfig.resolveBlock(SpiralModConfig.getNetherDividerBlock(), Blocks.BLACKSTONE);
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
                this.overworldBlock = config.getOverworldBlock();
                this.netherBlock = config.getNetherBlock();
                this.currentWallHeight = config.wallHeight();
                this.currentStyle = SpiralPresetStyle.fromConfig(config);
            }
        }
        LevelStem netherStem = context.selectedDimensions().dimensions().get(LevelStem.NETHER);
        if (netherStem != null && netherStem.generator() instanceof NoiseBasedChunkGenerator netherNoiseGen) {
            if (netherNoiseGen.getBiomeSource() instanceof SpiralBiomeSource netherSource) {
                this.enableNether = true;
                this.netherBlock = netherSource.getConfig().getNetherBlock();
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
        int startY = Math.max(22, this.height / 6 - 12);

        // タイトル
        this.addRenderableWidget(new StringWidget(
                centerX - 120,
                startY - 18,
                240,
                18,
                this.title,
                this.font
        ));

        // --- 左ペイン: コントロール ---
        int rowH = 20;

        // 行1: ディメンション適用切り替え (オーバーワールド / ネザー)
        int dimBtnW = (controlsWidth - 4) / 2;
        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableOverworld)
                .create(
                        leftX,
                        startY,
                        dimBtnW,
                        19,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_overworld"),
                        (button, value) -> {
                            this.enableOverworld = value;
                            if (value) {
                                this.setPreviewDimension("overworld");
                            }
                        }
                ));

        this.addRenderableWidget(CycleButton.onOffBuilder(this.enableNether)
                .create(
                        leftX + dimBtnW + 4,
                        startY,
                        dimBtnW,
                        19,
                        Component.translatable("createWorld.customize.logarithmic_spiral.enable_nether"),
                        (button, value) -> {
                            this.enableNether = value;
                            if (value) {
                                this.setPreviewDimension("nether");
                            }
                        }
                ));

        // 行2: 螺旋タイプ（プリセット）
        this.styleButton = this.addRenderableWidget(CycleButton.builder(SpiralPresetStyle::displayName, this.currentStyle)
                .withValues(SpiralPresetStyle.values())
                .create(
                        leftX,
                        startY + rowH,
                        controlsWidth,
                        19,
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
                19,
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
                19,
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
                        19,
                        Component.translatable("createWorld.customize.logarithmic_spiral.divider"),
                        (button, value) -> {
                            this.dividerEnabled = value;
                            if (this.overworldBlockButton != null) this.overworldBlockButton.active = value;
                            if (this.netherBlockButton != null) this.netherBlockButton.active = value;
                            if (this.wallHeightButton != null) this.wallHeightButton.active = value;
                            this.refreshPreview();
                        }
                ));

        // 行6: オーバーワールド仕切りブロック選択ボタン
        this.overworldBlockButton = this.addRenderableWidget(Button.builder(
                this.getOverworldButtonText(),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.gui.setScreen(new SpiralBlockPickerScreen(
                                this,
                                Component.translatable("createWorld.customize.logarithmic_spiral.select_overworld_block"),
                                this.overworldBlock,
                                selected -> {
                                    this.overworldBlock = selected;
                                    this.updateBlockButtonLabels();
                                    this.setPreviewDimension("overworld");
                                }
                        ));
                    }
                }
        ).bounds(leftX, startY + rowH * 5, controlsWidth, 19).build());
        this.overworldBlockButton.active = this.dividerEnabled;

        // 行7: ネザー仕切りブロック選択ボタン
        this.netherBlockButton = this.addRenderableWidget(Button.builder(
                this.getNetherButtonText(),
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.gui.setScreen(new SpiralBlockPickerScreen(
                                this,
                                Component.translatable("createWorld.customize.logarithmic_spiral.select_nether_block"),
                                this.netherBlock,
                                selected -> {
                                    this.netherBlock = selected;
                                    this.updateBlockButtonLabels();
                                    this.setPreviewDimension("nether");
                                }
                        ));
                    }
                }
        ).bounds(leftX, startY + rowH * 6, controlsWidth, 19).build());
        this.netherBlockButton.active = this.dividerEnabled;

        // 行8: 壁の高さ (0: 敷石, 1〜5)
        this.wallHeightButton = this.addRenderableWidget(CycleButton.<Integer>builder(
                        val -> val == 0
                                ? Component.translatable("createWorld.customize.logarithmic_spiral.wall_height.flat")
                                : Component.literal(String.valueOf(val)),
                        this.currentWallHeight)
                .withValues(0, 1, 2, 3, 4, 5)
                .create(
                        leftX,
                        startY + rowH * 7,
                        controlsWidth,
                        19,
                        Component.translatable("createWorld.customize.logarithmic_spiral.wall_height"),
                        (button, value) -> this.currentWallHeight = value
                ));
        this.wallHeightButton.active = this.dividerEnabled;

        // --- 右ペイン: リアルタイムマッププレビュー ---
        int previewY = startY + 16;
        this.previewDimensionButton = this.addRenderableWidget(CycleButton.<String>builder(
                        val -> Component.literal("表示: " + ("nether".equals(val) ? "Nether" : "Overworld")),
                        this.previewDimension)
                .withValues("overworld", "nether")
                .create(
                        previewX,
                        previewY - 18,
                        128,
                        16,
                        Component.empty(),
                        (button, value) -> {
                            this.previewDimension = value;
                            this.refreshPreview();
                        }
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

    private Component getOverworldButtonText() {
        return Component.translatable("createWorld.customize.logarithmic_spiral.overworld_block_btn", this.overworldBlock.getName());
    }

    private Component getNetherButtonText() {
        return Component.translatable("createWorld.customize.logarithmic_spiral.nether_block_btn", this.netherBlock.getName());
    }

    private void updateBlockButtonLabels() {
        if (this.overworldBlockButton != null) {
            this.overworldBlockButton.setMessage(this.getOverworldButtonText());
        }
        if (this.netherBlockButton != null) {
            this.netherBlockButton.setMessage(this.getNetherButtonText());
        }
    }

    private void markCustom() {
        if (this.currentStyle != SpiralPresetStyle.CUSTOM) {
            this.currentStyle = SpiralPresetStyle.CUSTOM;
            if (this.styleButton != null) {
                this.styleButton.setValue(SpiralPresetStyle.CUSTOM);
            }
        }
    }

    private void setPreviewDimension(String dim) {
        this.previewDimension = dim;
        if (this.previewDimensionButton != null) {
            this.previewDimensionButton.setValue(dim);
        }
        this.refreshPreview();
    }

    private void refreshPreview() {
        if (this.previewWidget != null) {
            this.previewWidget.updatePreview(this.getCurrentConfig(), this.dividerEnabled, this.previewDimension);
        }
    }

    private SpiralConfig getCurrentConfig() {
        double boundaryHalfWidth = this.dividerEnabled ? 0.5D : 0.0D;
        String owId = BuiltInRegistries.BLOCK.getKey(this.overworldBlock).toString();
        String netherId = BuiltInRegistries.BLOCK.getKey(this.netherBlock).toString();
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
                owId,
                netherId
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Modern UI の背景カード（左右のパネルを覆う洗練されたダークグラス風カード）
        int centerX = this.width / 2;
        int totalWidth = 356;
        int cardX = centerX - totalWidth / 2;
        int startY = Math.max(22, this.height / 6 - 12);
        int cardH = 184;

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

        // グローバルコンフィグの永続化
        SpiralModConfig.setEnableOverworld(this.enableOverworld);
        SpiralModConfig.setEnableNether(this.enableNether);
        SpiralModConfig.setDividerBlocks(this.dividerEnabled);
        SpiralModConfig.setOverworldDividerBlock(BuiltInRegistries.BLOCK.getKey(this.overworldBlock).toString());
        SpiralModConfig.setNetherDividerBlock(BuiltInRegistries.BLOCK.getKey(this.netherBlock).toString());
        SpiralModConfig.setWallHeight(this.currentWallHeight);
        SpiralModConfig.setScale(this.currentScale);
        SpiralModConfig.setGrowth(this.currentGrowth);
        SpiralModConfig.setStyle(this.currentStyle);
        SpiralModConfig.save();

        WorldCreationContext.DimensionsUpdater updater = (registryAccess, worldDimensions) -> {
            Map<ResourceKey<LevelStem>, LevelStem> newDimensions = new LinkedHashMap<>(worldDimensions.dimensions());
            HolderGetter<MultiNoiseBiomeSourceParameterList> paramLists =
                    registryAccess.lookupOrThrow(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : newDimensions.entrySet()) {
                ResourceKey<LevelStem> key = entry.getKey();
                LevelStem stem = entry.getValue();

                if (key.equals(LevelStem.OVERWORLD)) {
                    if (this.enableOverworld) {
                        entry.setValue(updateStemWithSpiral(stem, newConfig, "overworld", registryAccess));
                    } else {
                        entry.setValue(updateStemWithVanilla(stem, paramLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)));
                    }
                } else if (key.equals(LevelStem.NETHER)) {
                    if (this.enableNether) {
                        entry.setValue(updateStemWithSpiral(stem, newConfig, "nether", registryAccess));
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

    private static LevelStem updateStemWithSpiral(LevelStem stem, SpiralConfig config, String dimension, RegistryAccess registryAccess) {
        if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen) {
            HolderGetter<Biome> biomeLookup = registryAccess.lookupOrThrow(Registries.BIOME);
            boolean isNether = "nether".equalsIgnoreCase(dimension);
            Identifier boundaryBiomeId = isNether
                    ? Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "spiral_boundary_nether")
                    : Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "spiral_boundary");
            Optional<Holder<Biome>> boundaryBiomeOpt = biomeLookup.get(ResourceKey.create(Registries.BIOME, boundaryBiomeId))
                    .map(ref -> (Holder<Biome>) ref);

            HolderSet<Biome> biomes;
            if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                biomes = spiralSource.getBiomes();
                if (boundaryBiomeOpt.isEmpty()) {
                    boundaryBiomeOpt = spiralSource.getBoundaryBiomeOpt();
                }
            } else {
                List<Holder<Biome>> collected = noiseGen.getBiomeSource().possibleBiomes().stream().toList();
                if (collected.isEmpty()) {
                    TagKey<Biome> tagKey = isNether
                            ? TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "nether_biomes"))
                            : TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "overworld_biomes"));
                    biomes = biomeLookup.getOrThrow(tagKey);
                } else {
                    biomes = HolderSet.direct(collected);
                }
            }

            SpiralBiomeSource newSource = new SpiralBiomeSource(
                    biomes,
                    boundaryBiomeOpt,
                    config,
                    dimension
            );
            return new LevelStem(stem.type(), new NoiseBasedChunkGenerator(newSource, noiseGen.generatorSettings()));
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
