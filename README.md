# Logarithmic Spiral Biome

<p align="center">
  <img src="common/src/main/resources/logarithmicspiralbiome.png" alt="Logarithmic Spiral Biome Logo" width="128"/>
</p>

<p align="center">
  <b>Generates Minecraft worlds in an elegant Logarithmic Spiral pattern.</b><br>
  Available for <b>Fabric</b> and <b>NeoForge</b> (and Forge) | Minecraft 26.3+
</p>

<p align="center">
  <a href="#english">English</a> | <a href="#日本語">日本語</a>
</p>

---

<a name="english"></a>
## 🌟 Overview

**Logarithmic Spiral Biome** reorganizes Minecraft world generation using the mathematical beauty of logarithmic spirals ($r = a \cdot e^{b\theta}$). Instead of random noise blobs, biomes are distributed in continuous curving arms expanding outwards from the world origin (`X=0, Z=0`).

Whether you are exploring peaceful meadows or plunging into the depths of the Nether, each spiral arm takes you on a predictable yet endless journey across diverse climates!

---

## ✨ Features (v1.1.0)

### 🌀 Overworld & Nether Spiral Generation
- **Overworld**: Experience oceans, plains, forests, and mountain ranges alternating in graceful spiral ribbons.
- **The Nether (New in v1.1.0!)**: Traverse the Nether in curving spiral sectors, fully integrated with boundary block dividers.
- Toggle Overworld and Nether spiral generation independently!

### 🤝 Full Compatibility with Major Biome Mods
Seamlessly integrates with popular world generation and biome mods:
- **Terralith**: Epic vanilla-style landscapes, canyons, and volcanoes arranged along spirals.
- **Biomes O' Plenty**: Rich flora, unique wood types, and colorful fantasy biomes in distinct sectors.
- **Incendium**: Custom Nether terrain, monumental dungeons, and volcanic biomes adapted into the Nether spiral.
- **Promenade & TerraBlender**: Smoothly handles dynamic biome injection and custom tags.

### 🎨 In-Game Customization Screen & Live Map Preview
Click **Customize** when selecting the `Logarithmic Spiral` world preset to open a modern configuration GUI:
- **Interactive Live Map Preview**: Real-time 2D color preview of your spiral layout. Toggle between Overworld and Nether views with grid overlay!
- **Spiral Preset Styles**:
  - **Standard**: Balanced arm spacing for regular survival gameplay.
  - **Golden Ratio ($\phi$)**: Natural proportions modeled after sunflower seeds and galaxy spirals.
  - **Tight**: Rapidly alternating biomes for fast-paced exploration.
  - **Loose**: Broad, sweeping regions for sprawling empires and large builds.
  - **Custom**: Full manual control over scale and growth.
- **Fine-Tuning Sliders**:
  - **Scale**: Initial core size ($0.5$ to $16.0$).
  - **Growth Rate**: Expansion velocity per revolution ($0.10$ to $0.70$).
- **Custom Divider Blocks & Walls**:
  - **Block Picker Screen**: Search and pick any solid block from creative categories (Stone, Obsidian, Blackstone, Glass, etc.).
  - **Distinct Blocks for Overworld & Nether**: e.g., Smooth Stone for Overworld, Gilded Blackstone for the Nether.
  - **Wall Height**: Choose from flat floor paving (0) up to 5-block tall walls separating adjacent spiral sectors.

### 🌐 Multi-Language Support
- Full localization for **English** and **Japanese (日本語)**.

---

## 🚀 How to Use

1. Launch Minecraft with **Logarithmic Spiral Biome** installed.
2. Click **Create New World**.
3. Under **World Type**, select **Logarithmic Spiral**.
4. *(Optional)* Click **Customize** to open the preview and adjust spiral parameters, blocks, and wall heights.
5. Create your world and start exploring!

---

<a name="日本語"></a>
## 🌟 概要（日本語）

**Logarithmic Spiral Biome** は、対数螺旋の方程式（$r = a \cdot e^{b\theta}$）を用いて、マインクラフトのバイオームを世界中心（`X=0, Z=0`）から外側へと広がる美しい螺旋状に再配置するワールド生成MODです。

---

## ✨ 主な機能（v1.1.0）

### 🌀 オーバーワールド & ネザー両対応
- **オーバーワールド**: 平原、森林、山岳、海洋が弧を描いて無限に広がる螺旋世界。
- **ネザー（v1.1.0新機能）**: ネザーの各バイオームも対数螺旋のセクターとして生成。オーバーワールドとネザーそれぞれで個別に有効/無効を切り替え可能。

### 🤝 人気バイオームMODとの完全共存
- **Terralith**: 雄大な山脈や峡谷などの景観をそのまま螺旋状に配置。
- **Biomes O' Plenty**: 多彩な樹木や植物が螺旋の帯ごとに鮮やかに変化。
- **Incendium**: ネザーの大規模地形・ダンジョン・独自バイオーム（Toxic HeapやQuartz Flats等）を100%活かしたまま対数螺旋化。
- **Promenade / TerraBlender**: 互換性レイヤーを通じて他MODのカスタムバイオームを自動収集・最適配置。

### 🎨 カスタマイズ画面 & リアルタイムマッププレビュー
ワールド新規作成時に「カスタマイズ」ボタンを押すことで、専用のGUIが開きます：
- **リアルタイムマッププレビュー**: スライダーやプリセットの変更が即座に右側のミニマップに反映。オーバーワールドとネザーのプレビュー切り替え対応。
- **螺旋プリセットスタイル**:
  - **標準 (Standard)**: バランスの取れた探索向け。
  - **黄金比 (Golden)**: ひまわりの種や銀河の腕のような自然美あふれる比率。
  - **密集 (Tight)**: バイオームが次々と素早く切り替わる設定。
  - **広域 (Loose)**: 1つ1つのバイオーム帯が広く、巨大建築や国造りに最適。
  - **カスタム (Custom)**: スライダーで自由自在に設定。
- **仕切りブロック選択（ブロックピッカー）**:
  - プリセット固定ではなく、クリエイティブタブ風の一覧画面からバニラ・MOD問わず好きなブロックを自由に検索・選択可能。
  - オーバーワールド用（石など）とネザー用（ブラックストーンなど）を個別に指定可能。
- **壁の高さ調整**: 敷石フラット（0）〜高さ5ブロックの境界壁まで自由に変更可能。

### 🌐 多言語対応
- **英語** および **日本語** に完全対応。

---

## 📜 ライセンス
MIT License
