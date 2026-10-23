# Logarithmic Spiral Biome

![Banner](https://cdn.modrinth.com/data/cached_images/c4b27e5d4c4d295a53250d207a73c9dceb291a30_0.webp)

A Minecraft mod for **Fabric** and **NeoForge** that generates Overworld and Nether biomes in a mathematical logarithmic spiral pattern.

---

## Overview

Unlike standard Minecraft world generation which relies on random multi-noise blobs, **Logarithmic Spiral Biome** distributes biomes along continuous logarithmic spiral arms ($r = a \cdot e^{b\theta}$) emanating outward from the world origin point (`X=0, Z=0`).

Each biome occupies a curving radial sector that expands progressively as you travel farther from spawn, creating a structured, navigable, and aesthetically striking world.

---

## Features

### Core World Generation
- **Logarithmic Spiral Preset**: Select the `Logarithmic Spiral` world type when creating a new world.
- **Deterministic Biome Distribution**: Biomes are sorted and mapped to spiral sectors centered at coordinates `0, 0`.

### Added in v1.1.0
- **The Nether Dimension Support**:
  - The Nether can now generate in a logarithmic spiral layout just like the Overworld.
  - Independent toggles allow enabling or disabling spiral generation separately for the Overworld and the Nether.
- **In-Game Customization Screen**:
  - Accessible via the **Customize** button on the world creation screen.
  - **Real-Time 2D Map Preview**: Interactive color-coded preview of the spiral biome layout with chunk grid overlay and scale indicator. Toggle between Overworld and Nether preview modes.
  - **Preset Styles**: Choose from carefully tuned presets:
    - `Standard`: Balanced arm spacing suited for regular survival gameplay.
    - `Golden Ratio`: Natural spiral geometry based on the golden ratio ($\phi \approx 1.618$).
    - `Tight`: Rapidly alternating biomes for fast-paced exploration.
    - `Loose`: Broad, expansive biome regions ideal for large builds and kingdoms.
    - `Custom`: Fully manual control over scale and growth parameters.
  - **Fine-Tuning Sliders**: Adjust scale (initial radius) and growth rate (arm expansion speed).
- **Boundary Divider Blocks & Wall Height**:
  - Optional physical boundary walls mark the borders between adjacent spiral arms.
  - **Searchable Block Picker**: Select any solid block from creative inventory categories (e.g., Stone, Obsidian, Blackstone).
  - Separate divider block configurations for the Overworld and the Nether.
  - **Adjustable Wall Height**: Choose from flat floor paving (height 0) up to 5-block tall partition walls.
- **Comprehensive Biome Mod Compatibility**:
  - Seamlessly extracts and wraps biomes from major world generation mods:
    - **Terralith**: Custom terrain, mountains, and vanilla-style biomes follow the spiral ribbons.
    - **Incendium**: Custom Nether terrain, monumental structures, and volcanic biomes wrap into spiral sectors.
    - **Biomes O' Plenty**: New foliage, trees, and fantasy biomes cleanly distributed across spiral arms.
    - **Promenade & TerraBlender**: Smooth dynamic biome registration and injection.

---

## Server Configuration

To use this world preset on a dedicated server, configure `server.properties`:

```properties
level-name=world
level-seed=
level-type=logarithmicspiralbiome:logarithmic_spiral
```

### Config File (`config/logarithmicspiralbiome.json`)

Server-side generation parameters and divider block settings can be customized in `config/logarithmicspiralbiome.json`:

```json
{
  "enableOverworld": true,
  "enableNether": true,
  "style": "STANDARD",
  "scale": 3.0,
  "growth": 0.32,
  "dividerBlocks": true,
  "overworldDividerBlock": "minecraft:stone",
  "netherDividerBlock": "minecraft:blackstone",
  "wallHeight": 3
}
```

---

## Supported Versions

- **Minecraft**: 26.3+ (also available for 1.20.1, 1.20.4, 1.20.6, 1.21.1, 1.21.4, 1.21.11, 26.1)
- **Mod Loaders**: Fabric, NeoForge

---

## License

This project is licensed under the [MIT License](LICENSE).
