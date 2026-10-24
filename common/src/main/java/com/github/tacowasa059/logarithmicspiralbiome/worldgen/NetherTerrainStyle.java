package com.github.tacowasa059.logarithmicspiralbiome.worldgen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public enum NetherTerrainStyle {
    GENTLE("gentle", Identifier.fromNamespaceAndPath("logarithmicspiralbiome", "nether_spiral")),
    VANILLA("vanilla", Identifier.fromNamespaceAndPath("minecraft", "nether"));

    private final String key;
    private final Identifier settingsId;

    NetherTerrainStyle(String key, Identifier settingsId) {
        this.key = key;
        this.settingsId = settingsId;
    }

    public String getKey() {
        return this.key;
    }

    public Identifier getSettingsId() {
        return this.settingsId;
    }

    public Component displayName() {
        return Component.translatable("createWorld.customize.logarithmic_spiral.nether_terrain." + this.key);
    }

    public static NetherTerrainStyle fromKey(String key) {
        for (NetherTerrainStyle style : values()) {
            if (style.key.equalsIgnoreCase(key)) {
                return style;
            }
        }
        return GENTLE;
    }
}
