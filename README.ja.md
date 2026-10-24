[English](README.md)

# 対数螺旋バイオーム (Logarithmic Spiral Biome)

![Banner](https://cdn.modrinth.com/data/cached_images/c4b27e5d4c4d295a53250d207a73c9dceb291a30_0.webp)

**Fabric** および **NeoForge** 向け Minecraft MOD。オーバーワールドおよびネザーのバイオームを数学的な対数螺旋パターンに沿って生成します。

---

## 概要

バニラの複雑なマルチノイズによるバイオーム分布とは異なり、ワールド原点（`X=0, Z=0`）から外側へ放射状に伸びる連続的な対数螺旋（$r = a \cdot e^{b\theta}$）に沿ってバイオームが配置されます。

各バイオームは原点から離れるにつれて幅を広げるリボン状の領域を形成し、探索しやすく幾何学的に美しい世界を作り出します。

---

## 主な機能

### コア機能
- **対数螺旋プリセット**: ワールド新規作成時にワールドタイプ「対数螺旋」を選択してプレイ可能。
- **規則的なバイオーム配置**: 座標 `0, 0` を中心とした螺旋セクターにバイオームが決定論的に配置。

### v1.1.1 追加機能
- **スポーン底上げスライダー**:
  - 原点周辺の海・水没を防ぐため、陸地を海面（Y=64〜67）以上に底上げ。
  - ワールドカスタマイズ画面およびコンフィグから調整可能（`オフ`、または `10m` 〜 `300m` の範囲で 10m 刻み）。
  - 高速・安全なチャンク生成処理（自己デッドロックの解消）。
- **ネザー地形スタイルの選択**:
  - ネザーの地形生成規則を2種類から選択可能：
    - **現世風（緩やかな起伏）**: 岩盤天井を維持したまま、地上のような移動しやすい緩やかな起伏を生成。
    - **バニラ**: 従来の洞窟迷路地形。
- **マルチローダー構成の適正化**: Fabric および NeoForge に集中対応（26.x 系の Forge は除外）。

### v1.1.0 追加機能
- **ネザーディメンション対応**:
  - ネザーもオーバーワールド同様に対数螺旋状にバイオームを生成可能。
  - オーバーワールド／ネザーの適用有無を個別に切り替え可能。
- **ゲーム内カスタマイズ画面**:
  - ワールド作成画面の「カスタマイズ」ボタンから設定可能。
  - **リアルタイム 2D マッププレビュー**: 螺旋バイオームの境界とチャンクグリッドをカラープレビュー。
  - **プリセットスタイル**: 用途に合わせた螺旋プリセット（標準、広大、高密度、急旋回、カスタム）。
  - **詳細調整スライダー**: スケール（初期半径）や成長率を自由に微調整。
- **仕切りブロック＆壁の高さ設定**:
  - 螺旋の境界線上に仕切りブロックを設置可能。
  - 検索対応ブロックピッカーから任意の固体ブロックを選択可能。
  - 壁の高さを敷石（高さ 0）から 5 ブロックの壁まで設定可能。
- **各種バイオーム追加 MOD との互換性**:
  - Terralith, Incendium, Biomes O' Plenty などの追加バイオームにも自動対応。

---

## サーバー設定

専用サーバー（Dedicated Server）で使用する場合は、`server.properties` を以下のように設定してください：

```properties
level-name=world
level-seed=
level-type=logarithmicspiralbiome:logarithmic_spiral
```

### 設定ファイル (`config/logarithmicspiralbiome.json`)

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

## 対応バージョン

- **Minecraft**: 26.2
- **Mod Loaders**: Fabric, NeoForge

---

## ライセンス

本プロジェクトは [MIT License](LICENSE) のもとで公開されています。
