package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.biome.SpiralBiomeSource;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpiralPresetConfigScreen extends Screen {
    private final CreateWorldScreen parent;
    private final WorldCreationContext context;

    private SpiralPresetStyle currentStyle = SpiralPresetStyle.STANDARD;
    private boolean dividerEnabled = false;

    public SpiralPresetConfigScreen(CreateWorldScreen parent, WorldCreationContext context) {
        super(Component.translatable("createWorld.customize.logarithmic_spiral.title"));
        this.parent = parent;
        this.context = context;

        // 初期設定を現在のワールドから読み取る（存在する場合）
        if (context.selectedDimensions().overworld() instanceof NoiseBasedChunkGenerator noiseGen) {
            if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                SpiralConfig config = spiralSource.getConfig();
                this.currentStyle = SpiralPresetStyle.fromConfig(config);
                this.dividerEnabled = config.boundaryHalfWidth() > 0.0D;
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 6;

        // タイトル
        this.addRenderableWidget(new StringWidget(
                centerX - 100,
                startY,
                200,
                20,
                this.title,
                this.font
        ));

        // 螺旋スタイル選択
        this.addRenderableWidget(CycleButton.builder(SpiralPresetStyle::displayName, this.currentStyle)
                .withValues(SpiralPresetStyle.values())
                .create(
                        centerX - 100,
                        startY + 30,
                        200,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.style"),
                        (button, value) -> this.currentStyle = value
                ));

        // 仕切りブロック選択（ON / OFF）
        this.addRenderableWidget(CycleButton.onOffBuilder(this.dividerEnabled)
                .create(
                        centerX - 100,
                        startY + 54,
                        200,
                        20,
                        Component.translatable("createWorld.customize.logarithmic_spiral.divider"),
                        (button, value) -> this.dividerEnabled = value
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

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }

    private void applyAndClose() {
        SpiralConfig newConfig = this.currentStyle.toConfig(this.dividerEnabled);
        WorldCreationContext.DimensionsUpdater updater = (registryAccess, worldDimensions) -> {
            Map<ResourceKey<LevelStem>, LevelStem> newDimensions = new LinkedHashMap<>(worldDimensions.dimensions());
            for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : newDimensions.entrySet()) {
                LevelStem stem = entry.getValue();
                if (stem.generator() instanceof NoiseBasedChunkGenerator noiseGen) {
                    if (noiseGen.getBiomeSource() instanceof SpiralBiomeSource spiralSource) {
                        SpiralBiomeSource newSource = new SpiralBiomeSource(
                                spiralSource.getBiomes(),
                                spiralSource.getBoundaryBiomeOpt(),
                                newConfig
                        );
                        NoiseBasedChunkGenerator newGen = new NoiseBasedChunkGenerator(newSource, noiseGen.generatorSettings());
                        entry.setValue(new LevelStem(stem.type(), newGen));
                    }
                }
            }
            return new WorldDimensions(newDimensions);
        };

        this.parent.getUiState().updateDimensions(updater);
        this.onClose();
    }
}
