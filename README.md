# JIG-ERD

[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.github.irof/jig-erd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.irof/jig-erd)
[![GitHub Actions](https://github.com/irof/jig-erd/workflows/CI/badge.svg)](https://github.com/irof/jig-erd/actions?query=workflow%3ACI)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/805m3sh5reap9pbx/branch/master?svg=true)](https://ci.appveyor.com/project/irof/jig-erd/branch/master)

ざっくりなER図を出力します。
現在はH2DatabaseとPostgreSQLに対応しています。

関連だけに着目したデータベース設計の補助道具です。

PKや列のデータ型、その他制約と言った一般的なER図で必須な項目は扱っていません。対応予定もないです。詳細な情報は他のツールの使用をお勧めします。

- [Schema Spy](https://github.com/schemaspy/schemaspy)
- [tbls](https://github.com/k1LoW/tbls)
- [planter](https://github.com/achiku/planter)
- [PlantERD](https://github.com/sue445/plant_erd)

## サンプル

以下は [system-sekkei/library](https://github.com/system-sekkei/library) で出力したサンプルです。3種類のダイアグラムが出力されます。

- `jig-erd-overview.svg`: スキーマのみ
- `jig-erd-summary.svg`: スキーマ、テーブルのみ
- `jig-erd-detail.svg`: スキーマ、テーブル、外部キーカラム

ファイル名、形式は設定で変更できます。

### スキーマのみ
![overview](./document/library-erd-overview.png)

### スキーマ、テーブルのみ
テーブル名を `_` ではじめると色が変わります。

![summary](./document/library-erd-summary.png)

### スキーマ、テーブル、外部キーカラム
![detail](./document/library-erd-detail.png)

## 前提条件

- Java21以降がインストールされていること
- Graphvizがインストールされていること
    - 出力形式を `DOT` にした場合は不要。この場合は自分で変換を行ってください。

### 対象バージョン
現在の最新は `0.2.X` です。

|対象 | 推奨      |
|----|---------|
|Java| 21以降    |
|Graphviz| 12.2.1  |
|H2 Database Engine| 2.3.232 |
|PostgreSQL | 11.1    |

- Java11-16で使用する場合は `0.0.X` ( `0.0.16` など) を使用してください
- Java17-20で使用する場合は `0.1.0` を使用してください
- H2 Database Engine `1.4.200` 以前を対象にする場合、jig-erd `0.0.11` 以前を使用してください。


#### 動作確認環境

| JIG     | OS                           | Java                             | Graphviz |
|---------|------------------------------|----------------------------------|----------|
| `0.1.0` | AppVeyor visual studio 2022  | `17`                             | `7.0.6`  |
| `0.1.0` | GitHub Actions ubuntu-latest | `17`                             | `2.42.2` |
| `0.0.3` | `macOS Catalina 10.15.5`     | `AdoptOpenJDK 11.0.7`            | `2.44.1` |
| `0.0.3` | `Windows 10`                 | `jdk-14.0.2_windows-x64_bin.exe` | `2.44.1` |
| `0.0.3` | GitHub Actions ubuntu-latest | `11`                             | `2.40.1` |
| `0.0.3` | AppVeyor visual studio 2015  | `11`                             | `2.38.0` |

## Getting Started

### jig-erd-spring-boot-autoconfigure

webアプリケーションの場合、dependencyに追加して起動するだけです。

```
<dependencies>
    <dependency>
        <groupId>com.github.irof</groupId>
        <artifactId>jig-erd-spring-boot-autoconfigure</artifactId>
        <version>0.2.1</version>
    </dependency>
</dependencies>
```

- `/jig-erd` をブラウザで開くとMermaidのダイアグラムが出ます。
    - Graphvizとは出力内容が異なります。
    - Mermaidをメインにするかは日本語対応が微妙なので未定です。
- `jig.erd.enabled=false` で無効になります。

### コマンドラインから使う

既にテーブル作成済みのDBに対して実行したい場合。

- jig-erd-x.x.x.jarをダウンロード
  - [Maven Central Repository](https://repo1.maven.org/maven2/com/github/irof/jig-erd/) などから
- JDBCドライバ（ `postgresql-42.2.14.jar` など ）をダウンロード
    - [JDBCドライバダウンロードページ](https://jdbc.postgresql.org/) などから
- 実行

```
java -cp jig-erd-x.x.x.jar:postgresql-42.2.14.jar \
    jig.erd.JigErd {url} {user} {password}
```
url, user, passは適宜置き換えてください。urlは `jdbc:postgresql://localhost:5432/test` とかです。

### Spring Boot Testで使う

SpringBootが `schema.sql` などを使用してセットアップしたDBのERDを出力します。
依存に追加して、テスト経由で実行します。

Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'com.github.irof:jig-erd:latest.release'
}
```

Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.github.irof</groupId>
        <artifactId>jig-erd</artifactId>
        <version>0.2.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

```java
@SpringBootTest
public class Erd {

    @Test
    void run(@Autowired DataSource dataSource) {
        JigErd.run(dataSource);
    }
}
```

他の出力例は [wiki](https://github.com/irof/jig-erd/wiki) を参照してください。

## 設定

`jig.properties` ファイルをクラスパス、実行時のカレントディレクトリ、ユーザーホームの `.jig` ディレクトリに配置してください。 `run` メソッドを実行する場合は第二引数の `Map` でも指定できます。

```properties
# サンプル
jig.erd.output.directory=./build
jig.erd.output.prefix=library-er
jig.erd.output.format=png
```

|キー| 意味                                                                   | 許容する値                                   | 設定しない場合のデフォルト |
|----|----------------------------------------------------------------------|-----------------------------------------|---------------|
|`jig.erd.output.directory` | 出力先ディレクトリ                                                            | 任意のディレクトリ                               | カレントディレクトリ    |
|`jig.erd.output.prefix` | 出力ファイル名のプレフィックス                                                      | 英数、記号（`-_.`）                            | `jig-erd`     |
|`jig.erd.output.format` | 出力ファイルの形式                                                            | `SVG`, `PNG`, `DOT`(テキスト)               | `SVG`         |
|`jig.erd.filter.schema.pattern` | 出力対象となるスキーマのフィルタ条件                                                   | `Pattern#compile(String)` でコンパイルできる正規表現 | フィルタしない（全て出力） |

ファイル名は `{jig.erd.output.prefix}-detail.{拡張子}` などになります。

正規表現にマルチバイト文字を使用する場合の `jig.properties` は `UTF-8` で記述してください。

### ダイアグラム全体の設定
`jig.erd.dot.root.{設定名}` で変更できます。

- `rankdir` ダイアグラムの方向です。デフォルトは `RL` [参考](https://graphviz.org/doc/info/attrs.html#d:rankdir) 
- `schemaColor` 全体に適用されるスキーマの色です デフォルトは `lightyellow`
- `entityColor` 全体に適用されるエンティティの色です。デフォルトは `lightgoldenrod`

使用できる色は [Graphviz](https://graphviz.org/doc/info/colors.html) を参照してください。

### ダイアグラム個別要素のカスタマイズ
`jig.erd.dot.custom.{任意のカスタマイズ名}` でノードの色などを設定できます。複数可能。優先順位は無いので対象指定で工夫してください。

- 対象の指定（いかのいずれか一つ）
  - `name-pattern` テーブル名に対する正規表現で指定します
  - `alias-pattern` 別名（COMMENTなどで指定する）に対する正規表現で指定します
  - `label-pattern` 表示されるラベル（aliasがある場合はalias、無い場合はname）に対する正規表現で指定します
- 設定できる属性
  - shape
  - fillcolor
  - color
  - penwidth
  - fontcolor
  - fontsize
  - width
  - height
  - fixedsize
  - margin

設定内容やデフォルト値などは [Graphvizのドキュメント](https://graphviz.org/doc/info/attrs.html) を参照してください。

デフォルトで以下の設定が入り、 `_` で始まるものがオレンジ色で表示されます。

```
jig.erd.dot.custom._.label-pattern=_.+
jig.erd.dot.custom._.fillcolor=orange
```

## リリース

- `gradle.properties` などで設定
  - 署名: `signing.keyId` `signing.password` `signing.secretKeyRingFile`
  - リポジトリの認証: `ossrhUsername`, `ossrhPassword`

```
VERSION=X.X.X ./gradlew publish
```

[oosrhのリポジトリマネージャー](https://s01.oss.sonatype.org/#stagingRepositories) からCloseとRelease

### SNAPSHOT
[SNAPSHOT](https://s01.oss.sonatype.org/content/repositories/snapshots/com/github/irof/jig-erd/)

## LICENSE

[Apache License 2.0](LICENSE)

