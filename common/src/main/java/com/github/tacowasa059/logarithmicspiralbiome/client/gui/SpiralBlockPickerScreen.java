package com.github.tacowasa059.logarithmicspiralbiome.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class SpiralBlockPickerScreen extends Screen {
    private final Screen parent;
    private final Block initialBlock;
    private final Consumer<Block> onSelect;

    private static List<Block> ALL_VALID_BLOCKS;
    private List<Block> filteredBlocks = new ArrayList<>();

    private EditBox searchBox;
    private int scrollOffset = 0;
    private static final int CELL_SIZE = 24;
    private int columns = 11;

    public SpiralBlockPickerScreen(Screen parent, Component title, Block initialBlock, Consumer<Block> onSelect) {
        super(title);
        this.parent = parent;
        this.initialBlock = initialBlock;
        this.onSelect = onSelect;
        initValidBlocks();
    }

    private static synchronized void initValidBlocks() {
        if (ALL_VALID_BLOCKS != null) return;
        List<Block> list = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block == Blocks.AIR || block instanceof LiquidBlock) continue;
            if (block.asItem() == Items.AIR) continue;
            list.add(block);
        }
        ALL_VALID_BLOCKS = list;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int cardWidth = 320;
        int startX = centerX - cardWidth / 2;
        this.columns = (cardWidth - 24) / CELL_SIZE;

        // 検索ボックス
        this.searchBox = new EditBox(this.font, startX + 12, 34, cardWidth - 24, 20, Component.translatable("createWorld.customize.logarithmic_spiral.search"));
        this.searchBox.setHint(Component.translatable("createWorld.customize.logarithmic_spiral.search_hint"));
        this.searchBox.setResponder(this::applyFilter);
        this.addRenderableWidget(this.searchBox);

        // 下部キャンセルボタン
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                .bounds(centerX - 75, this.height - 26, 150, 20)
                .build());

        this.applyFilter(this.searchBox.getValue());
    }

    private void applyFilter(String query) {
        if (ALL_VALID_BLOCKS == null) initValidBlocks();
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            this.filteredBlocks = new ArrayList<>(ALL_VALID_BLOCKS);
        } else {
            this.filteredBlocks = ALL_VALID_BLOCKS.stream()
                    .filter(b -> {
                        String name = b.getName().getString().toLowerCase(Locale.ROOT);
                        String id = BuiltInRegistries.BLOCK.getKey(b).toString().toLowerCase(Locale.ROOT);
                        return name.contains(q) || id.contains(q);
                    })
                    .toList();
        }
        this.scrollOffset = 0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int totalRows = (int) Math.ceil((double) this.filteredBlocks.size() / this.columns);
        int visibleRows = getVisibleRows();
        int maxScroll = Math.max(0, totalRows - visibleRows);

        if (verticalAmount > 0) {
            this.scrollOffset = Math.max(0, this.scrollOffset - 1);
            return true;
        } else if (verticalAmount < 0) {
            this.scrollOffset = Math.min(maxScroll, this.scrollOffset + 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == 0) {
            int gridLeft = this.width / 2 - (this.columns * CELL_SIZE) / 2;
            int gridTop = 60;
            int visibleRows = getVisibleRows();

            int mx = (int) event.x();
            int my = (int) event.y();

            for (int r = 0; r < visibleRows; r++) {
                int rowIndex = this.scrollOffset + r;
                for (int c = 0; c < this.columns; c++) {
                    int index = rowIndex * this.columns + c;
                    if (index >= this.filteredBlocks.size()) break;

                    int x = gridLeft + c * CELL_SIZE;
                    int y = gridTop + r * CELL_SIZE;

                    if (mx >= x && mx < x + CELL_SIZE && my >= y && my < y + CELL_SIZE) {
                        Block selected = this.filteredBlocks.get(index);
                        this.onSelect.accept(selected);
                        this.onClose();
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, isDouble);
    }

    private int getVisibleRows() {
        int gridTop = 60;
        int gridBottom = this.height - 34;
        return Math.max(1, (gridBottom - gridTop) / CELL_SIZE);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // カード背景
        int centerX = this.width / 2;
        int cardWidth = 320;
        int startX = centerX - cardWidth / 2;
        int cardH = this.height - 36;

        graphics.fill(startX - 1, 11, startX + cardWidth + 1, 11 + cardH + 1, 0x33FFFFFF);
        graphics.fill(startX, 12, startX + cardWidth, 12 + cardH, 0xEE10141A);

        // タイトル
        graphics.centeredText(this.font, this.title, centerX, 18, 0xFFFFFFFF);

        // グリッド描画
        int gridLeft = this.width / 2 - (this.columns * CELL_SIZE) / 2;
        int gridTop = 60;
        int visibleRows = getVisibleRows();

        Block hoveredBlock = null;

        for (int r = 0; r < visibleRows; r++) {
            int rowIndex = this.scrollOffset + r;
            for (int c = 0; c < this.columns; c++) {
                int index = rowIndex * this.columns + c;
                if (index >= this.filteredBlocks.size()) break;

                Block block = this.filteredBlocks.get(index);
                int x = gridLeft + c * CELL_SIZE;
                int y = gridTop + r * CELL_SIZE;

                boolean isHovered = mouseX >= x && mouseX < x + CELL_SIZE && mouseY >= y && mouseY < y + CELL_SIZE;
                boolean isCurrent = (block == this.initialBlock);

                // セル背景
                if (isCurrent) {
                    graphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, 0x5522AAEE);
                    graphics.fill(x, y, x + CELL_SIZE, y + 1, 0xFF44CCFF);
                    graphics.fill(x, y + CELL_SIZE - 1, x + CELL_SIZE, y + CELL_SIZE, 0xFF44CCFF);
                    graphics.fill(x, y, x + 1, y + CELL_SIZE, 0xFF44CCFF);
                    graphics.fill(x + CELL_SIZE - 1, y, x + CELL_SIZE, y + CELL_SIZE, 0xFF44CCFF);
                } else if (isHovered) {
                    graphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, 0x44FFFFFF);
                } else {
                    graphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, 0x22000000);
                }

                // アイテムアイコン
                graphics.fakeItem(new ItemStack(block), x + (CELL_SIZE - 16) / 2, y + (CELL_SIZE - 16) / 2);

                if (isHovered) {
                    hoveredBlock = block;
                }
            }
        }

        // スクロールバー
        int totalRows = (int) Math.ceil((double) this.filteredBlocks.size() / this.columns);
        if (totalRows > visibleRows) {
            int scrollbarX = gridLeft + this.columns * CELL_SIZE + 4;
            int scrollbarH = visibleRows * CELL_SIZE;
            graphics.fill(scrollbarX, gridTop, scrollbarX + 3, gridTop + scrollbarH, 0x44000000);

            int thumbH = Math.max(10, scrollbarH * visibleRows / totalRows);
            int thumbY = gridTop + (scrollbarH - thumbH) * this.scrollOffset / (totalRows - visibleRows);
            graphics.fill(scrollbarX, thumbY, scrollbarX + 3, thumbY + thumbH, 0xAAFFFFFF);
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // ホバーツールチップ
        if (hoveredBlock != null) {
            Component name = hoveredBlock.getName();
            String id = BuiltInRegistries.BLOCK.getKey(hoveredBlock).toString();
            graphics.setComponentTooltipForNextFrame(this.font, List.of(name, Component.literal(id).withStyle(s -> s.withColor(0x888888))), mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }
}
