# IdeaProjects 共通ガイド & プロジェクトルール

本プロジェクト（`LogarithmicSpiralBiome`）および親ディレクトリ（`C:\Users\PC_User\IdeaProjects\AGENTS.md`）で作業するエージェント向けの厳格な運用ルールです。

## 基本方針・重要原則
- **独断での push 厳禁**: ユーザーの明示的な指示（「pushして」等）があるまで、いかなる場合も `git push` は実行しない。
- **公開タスク（`publishMods`）の独断実行禁止**: ユーザーの許可なく Modrinth / CurseForge へのアップロードタスクを実行しない。
- **26.x 系のローダー構成**: Fabric および NeoForge のみ（Forge は完全除外）。

## テスト方針
- **ビルドの成功だけで完了としない**:
  - `build` 成功後に、ヘッドレスなサーバー起動テスト（`runServer` 等）を自動実行し、ワールド生成〜起動完了までエラーや例外、Mixin 競合、デッドロックがないか検証する。
- **手動確認事項の明示**:
  - 描画、GUI、ワールド生成の見た目など、人間でしか確認できない事項をユーザーに依頼する際は、確認手順と確認ポイントを具体的に記載する。

## ビルド成果物
- 最新の jar は、プロジェクトルート内の `jars/` ディレクトリに集約する。
- ファイル名規則: `{modid}-{modloader}-{minecraftversion}-{modversion}.jar`
- `jars/` は `.gitignore` に登録し、git には含めない。

## ドキュメント
- `README.md`（英語）および `README.ja.md`（日本語）の双方を管理する。
