package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralCalculator;
import com.github.tacowasa059.logarithmicspiralbiome.worldgen.SpiralConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SpiralPreviewWidget extends AbstractWidget implements AutoCloseable {
    private static final int PREVIEW_SIZE = 128;
    private static final int[] PALETTE = new int[]{
            0xFF2D882D, 0xFF55AA55, 0xFF88CC88, 0xFFDDAA55,
            0xFFCC7722, 0xFF994411, 0xFF2277AA, 0xFF4499CC,
            0xFF8844AA, 0xFFAA66CC, 0xFFCC88DD, 0xFFEEAA88,
            0xFF66AA77, 0xFF448866, 0xFF556677, 0xFF778899
    };

    private final Identifier textureId;
    private final DynamicTexture dynamicTexture;
    private final NativeImage image;
    private SpiralConfig lastConfig;
    private SpiralCalculator calculator;

    public SpiralPreviewWidget(int x, int y, int size) {
        super(x, y, size, size, Component.translatable("createWorld.customize.logarithmic_spiral.preview"));
        this.textureId = Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "spiral_preview_" + System.identityHashCode(this));
        this.image = new NativeImage(PREVIEW_SIZE, PREVIEW_SIZE, false);
        this.dynamicTexture = new DynamicTexture(() -> "spiral_preview", this.image);
        Minecraft.getInstance().getTextureManager().register(this.textureId, this.dynamicTexture);
    }

    public void updatePreview(SpiralConfig config, boolean dividerEnabled) {
        this.lastConfig = config;
        this.calculator = new SpiralCalculator(config);
        int half = PREVIEW_SIZE / 2;
        int blockStep = 4;
        int dividerColor = config.getOverworldPreviewColor();

        for (int py = 0; py < PREVIEW_SIZE; py++) {
            int blockZ = (py - half) * blockStep;
            boolean isZAxis = (py == half);
            boolean isZGrid = (Math.floorMod(py - half, 16) == 0);
            boolean isZDot = (Math.floorMod(py - half, 4) == 0);

            for (int px = 0; px < PREVIEW_SIZE; px++) {
                int blockX = (px - half) * blockStep;
                boolean isXAxis = (px == half);
                boolean isXGrid = (Math.floorMod(px - half, 16) == 0);
                boolean isXDot = (Math.floorMod(px - half, 4) == 0);

                int color;
                if (dividerEnabled && this.calculator.isBoundary(blockX, blockZ)) {
                    color = dividerColor;
                } else {
                    int region = this.calculator.regionIndex(blockX, blockZ, PALETTE.length);
                    color = PALETTE[Math.floorMod(region, PALETTE.length)];

                    // チャンクサイズ基準のグリッドオーバーレイ合成
                    if (isXAxis || isZAxis) {
                        // 中心十字軸 (X=0, Z=0)
                        int r = Math.min(255, ((color >> 16) & 0xFF) + 35);
                        int g = Math.min(255, ((color >> 8) & 0xFF) + 35);
                        int b = Math.min(255, (color & 0xFF) + 35);
                        color = (r << 16) | (g << 8) | b;
                    } else if (isXGrid || isZGrid) {
                        // 4チャンク（64m / 16px）ごとの境界グリッド線
                        int r = (int) (((color >> 16) & 0xFF) * 0.74);
                        int g = (int) (((color >> 8) & 0xFF) * 0.74);
                        int b = (int) ((color & 0xFF) * 0.74);
                        color = (r << 16) | (g << 8) | b;
                    } else if (isXDot && isZDot) {
                        // 1チャンク（16m / 4px）ごとの交差点ドット
                        int r = (int) (((color >> 16) & 0xFF) * 0.86);
                        int g = (int) (((color >> 8) & 0xFF) * 0.86);
                        int b = (int) ((color & 0xFF) * 0.86);
                        color = (r << 16) | (g << 8) | b;
                    }
                }

                // 原点マーカー (0, 0)
                if (Math.abs(px - half) <= 1 && Math.abs(py - half) <= 1) {
                    color = 0xFFFF3333; // 赤い原点マーカー
                }

                // ABGR 形式に変換
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int abgr = 0xFF000000 | (b << 16) | (g << 8) | r;
                this.image.setPixel(px, py, abgr);
            }
        }

        this.dynamicTexture.upload();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // モダンUIのカード背景・枠線
        int border = 2;
        int bgX = this.getX() - border;
        int bgY = this.getY() - border;
        int bgW = this.width + border * 2;
        int bgH = this.height + border * 2;

        graphics.fill(bgX - 1, bgY - 1, bgX + bgW + 1, bgY + bgH + 1, 0x44FFFFFF); // 外枠
        graphics.fill(bgX, bgY, bgX + bgW, bgY + bgH, 0xD011151B); // 背景カード

        // テクスチャ画像を描画 (x0, y0, x1, y1, u0, u1, v0, v1)
        graphics.blit(this.textureId, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0.0F, 1.0F, 0.0F, 1.0F);

        // 外枠のオーバーレイ
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, 0x55000000);
        graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, 0x55000000);
        graphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, 0x55000000);
        graphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, 0x55000000);

        // --- スケールバー HUD バッジ（左下: 16px = 64m / 4チャンク） ---
        net.minecraft.client.gui.Font font = Minecraft.getInstance().font;
        int scaleBarW = 16;
        int badgeX = this.getX() + 4;
        int badgeY = this.getY() + this.height - 16;

        // 半透明ダーク背景バッジ
        graphics.fill(badgeX - 1, badgeY - 2, badgeX + 68, badgeY + 12, 0xA0000000);

        // スケールバー線 (幅 16px)
        int barX = badgeX + 3;
        int barY = badgeY + 6;
        graphics.fill(barX, barY, barX + scaleBarW, barY + 1, 0xFFFFFFFF); // 横線
        graphics.fill(barX, barY - 2, barX + 1, barY + 3, 0xFFFFFFFF); // 左ヒゲ
        graphics.fill(barX + scaleBarW - 1, barY - 2, barX + scaleBarW, barY + 3, 0xFFFFFFFF); // 右ヒゲ

        // スケールバー文字 "64m (4ch)"
        graphics.text(font, Component.literal("64m (4ch)"), barX + scaleBarW + 3, badgeY + 2, 0xFFE0E0E0, false);

        // --- マウスホバー時のリアルタイム座標・チャンクツールチップ ---
        if (this.isHovered() && mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height) {
            int half = PREVIEW_SIZE / 2;
            int blockStep = 4;
            int localX = mouseX - this.getX();
            int localY = mouseY - this.getY();
            int blockX = (localX - half) * blockStep;
            int blockZ = (localY - half) * blockStep;
            int chunkX = blockX >> 4;
            int chunkZ = blockZ >> 4;

            java.util.List<Component> tooltip = java.util.List.of(
                    Component.translatable("createWorld.customize.logarithmic_spiral.tooltip.pos", blockX, blockZ),
                    Component.translatable("createWorld.customize.logarithmic_spiral.tooltip.chunk", chunkX, chunkZ),
                    Component.translatable("createWorld.customize.logarithmic_spiral.tooltip.region", this.calculator != null ? this.calculator.regionIndex(blockX, blockZ, PALETTE.length) + 1 : 1)
            );
            graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    @Override
    public void close() {
        Minecraft.getInstance().getTextureManager().release(this.textureId);
        this.dynamicTexture.close();
    }
}
