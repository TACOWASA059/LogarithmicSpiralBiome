package com.github.tacowasa059.logarithmicspiralbiome.worldgen.feature;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public enum DividerBlockType {
    STONE("stone", Blocks.STONE.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), 0xFFCCCCCC),
    STONE_BRICKS("stone_bricks", Blocks.STONE_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), 0xFFAAAAAA),
    POLISHED_DEEPSLATE("deepslate", Blocks.POLISHED_DEEPSLATE.defaultBlockState(), Blocks.POLISHED_BLACKSTONE.defaultBlockState(), 0xFF555555),
    GLASS("glass", Blocks.GLASS.defaultBlockState(), Blocks.TINTED_GLASS.defaultBlockState(), 0xAA88CCEE),
    OBSIDIAN("obsidian", Blocks.OBSIDIAN.defaultBlockState(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), 0xFF331144),
    SMOOTH_QUARTZ("quartz", Blocks.SMOOTH_QUARTZ.defaultBlockState(), Blocks.QUARTZ_BRICKS.defaultBlockState(), 0xFFFFFFFF),
    SEA_LANTERN("sea_lantern", Blocks.SEA_LANTERN.defaultBlockState(), Blocks.GLOWSTONE.defaultBlockState(), 0xFF77FFFF);

    private final String key;
    private final BlockState overworldState;
    private final BlockState netherState;
    private final int previewColor;

    DividerBlockType(String key, BlockState overworldState, BlockState netherState, int previewColor) {
        this.key = key;
        this.overworldState = overworldState;
        this.netherState = netherState;
        this.previewColor = previewColor;
    }

    public String getKey() {
        return this.key;
    }

    public BlockState getOverworldState() {
        return this.overworldState;
    }

    public BlockState getNetherState() {
        return this.netherState;
    }

    public int getPreviewColor() {
        return this.previewColor;
    }

    public Component displayName() {
        return Component.translatable("createWorld.customize.logarithmic_spiral.divider_type." + this.key);
    }

    public static DividerBlockType byKey(String key) {
        if (key == null) return STONE;
        for (DividerBlockType type : values()) {
            if (type.key.equalsIgnoreCase(key) || type.name().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return STONE;
    }
}
