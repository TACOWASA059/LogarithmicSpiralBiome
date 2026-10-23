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

    public SpiralPreviewWidget(int x, int y, int size) {
        super(x, y, size, size, Component.translatable("createWorld.customize.logarithmic_spiral.preview"));
        this.textureId = Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "spiral_preview_" + System.identityHashCode(this));
        this.image = new NativeImage(PREVIEW_SIZE, PREVIEW_SIZE, false);
        this.dynamicTexture = new DynamicTexture(() -> "spiral_preview", this.image);
        Minecraft.getInstance().getTextureManager().register(this.textureId, this.dynamicTexture);
    }

    public void updatePreview(SpiralConfig config, boolean dividerEnabled) {
        SpiralCalculator calculator = new SpiralCalculator(config);
        int half = PREVIEW_SIZE / 2;
        int blockStep = 4;

        for (int py = 0; py < PREVIEW_SIZE; py++) {
            int blockZ = (py - half) * blockStep;
            for (int px = 0; px < PREVIEW_SIZE; px++) {
                int blockX = (px - half) * blockStep;

                int color;
                if (dividerEnabled && calculator.isBoundary(blockX, blockZ)) {
                    color = 0xFFFFFFFF; // 境界線は白
                } else {
                    int region = calculator.regionIndex(blockX, blockZ, PALETTE.length);
                    color = PALETTE[Math.floorMod(region, PALETTE.length)];
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

        // テクスチャ画像を描画
        graphics.blit(this.textureId, this.getX(), this.getY(), this.width, this.height, 0.0F, 0.0F, 1.0F, 1.0F);

        // 外枠のオーバーレイ
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, 0x55000000);
        graphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, 0x55000000);
        graphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, 0x55000000);
        graphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, 0x55000000);
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
