[日本語](README.ja.md)

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
- **Deterministic Biome Distribution**: Biomes are mapped to spiral sectors centered at coordinates `0, 0`.

### Added in v1.1.1
- **Spawn Elevation Slider**:
  - Eliminates water submergence around the spawn point by raising terrain above sea level (Y=64–67).
  - Configurable in the customization screen and config: `OFF` or radius from `10m` to `300m` (step: 10m).
  - Fully optimized for fast, safe chunk generation.
- **Nether Terrain Style Option**:
  - Choose between two generation styles for the Nether:
    - **Overworld-like (Gentle)**: Generates gentle, traversable rolling surface hills while preserving the bedrock roof ceiling.
    - **Vanilla**: Standard cavernous maze generation.
- **Multiloader Streamlining**: Focused support on Fabric and NeoForge.

### Added in v1.1.0
- **The Nether Dimension Support**:
  - Nether biomes generate in a logarithmic spiral layout.
  - Independent toggles for enabling/disabling Overworld and Nether spiral generation.
- **In-Game Customization Screen**:
  - Accessible via the **Customize** button on the world creation screen.
  - **Real-Time 2D Map Preview**: Interactive color-coded preview of the spiral biome layout with chunk grid overlay.
  - **Preset Styles**: Choose from tuned presets (`Standard`, `Large`, `Dense`, `Tight`, `Custom`).
  - **Fine-Tuning Sliders**: Adjust scale (initial radius) and growth rate (arm expansion speed).
- **Boundary Divider Blocks & Wall Height**:
  - Optional physical boundary walls mark borders between adjacent spiral arms.
  - Searchable block picker for choosing any block as dividers.
  - Adjustable wall height from flat floor paving (0) up to 5-block tall walls.
- **Biome Mod Compatibility**:
  - Compatible with major world generation and biome mods (Terralith, Incendium, Biomes O' Plenty, etc.).

---

## Server Configuration

To use this world preset on a dedicated server, configure `server.properties`:

```properties
level-name=world
level-seed=
level-type=logarithmicspiralbiome:logarithmic_spiral
```

### Config File (`config/logarithmicspiralbiome.json`)

```json
{
  "enable_overworld": true,
  "enable_nether": true,
  "divider_blocks": false,
  "wall_height": 0,
  "overworld_divider_block": "minecraft:stone",
  "nether_divider_block": "minecraft:blackstone",
  "style": "standard",
  "scale": 3.0,
  "growth": 0.32,
  "spawn_elevate_radius": 100.0,
  "nether_terrain_style": "gentle"
}
```

---

## Supported Versions

- **Minecraft**: 26.1
- **Mod Loaders**: Fabric, NeoForge

---

## License

This project is licensed under the [MIT License](LICENSE).
