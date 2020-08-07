# JIG-ERD

[![GitHub Actions](https://github.com/irof/jig-erd/workflows/CI/badge.svg)](https://github.com/irof/jig-erd/actions?query=workflow%3ACI)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/805m3sh5reap9pbx/branch/master?svg=true)](https://ci.appveyor.com/project/irof/jig-erd/branch/master)

ざっくりなERダイアグラムを出力します。
現在はH2Databaseのみに対応しています。

テーブルの関連だけに着目した設計の補助道具です。
詳細な情報が欲しくなったら、他のツールを使用しましょう。

## サンプル

以下は [system-sekkei/library](https://github.com/system-sekkei/library) で出力したサンプルです。2種類のダイアグラムが出力されます。

- `jig-erd-summary.svg`: スキーマ、テーブルのみ
- `jig-erd-detail.svg`: スキーマ、テーブル、外部キーカラム

ファイル名、形式は設定で変更できます。

### スキーマ、テーブルのみ
![summary](./document/jig-er-summary.png)

### スキーマ、テーブル、外部キーカラム
![detail](./document/jig-er-detail.png)

## 前提条件

- Java11以降がインストールされていること
- Graphvizがインストールされていること

### versions

|対象 |推奨 |
|----|----|
|Java|11以降|
|Graphviz|最新| 2.44.1 |
|H2 Database Engine|最新| 1.4.200  |

#### 動作確認環境

|OS |Java |Graphviz |
|----|----|----|
|`macOS Catalina 10.15.5`| `AdoptOpenJDK 11.0.7`| `2.44.1` |
|`Windows 10`| `jdk-14.0.2_windows-x64_bin.exe`| `2.44.1` |
|GitHub Actions ubuntu-latest|`11` | `2.40.1` |
|AppVeyor visual studio 2015|`11` |`2.38.0` |


## Getting Started

依存関係に追加する。

```groovy
repositories {
    maven {
        url "https://dl.bintray.com/jignite/maven/"
    }
}

dependencies {
    testImplementation 'irof:jig-erd:+'
}
```

実行する。

```java
@SpringBootTest
public class Erd {

    @Test
    void run(@Autowired DataSource dataSource) {
        JigErd.run(dataSource);
    }
}
```

`javax.sql.DataSource` を使って出力します。
上記ではSpringBootTestを使用してテストコードで実行しています。
これはマイグレーションや`DataSource`をSpringBootに任せるためです。

他の出力例は [wiki](https://github.com/irof/jig-erd/wiki) を参照してください。

## 設定

`jig.properties` ファイルをクラスパスか実行時のカレントディレクトリに配置してください。

```properties
jig.erd.output.directory=./build
jig.erd.output.prefix=library-er
jig.erd.output.format=png
jig.erd.output.rankdir=LR
```

|キー|意味|許容する値|設定しない場合のデフォルト|
|----|----|----|----|
|`jig.erd.output.directory` |出力先ディレクトリ|任意のディレクトリ |カレントディレクトリ |
|`jig.erd.output.prefix` |出力ファイル名のプレフィックス |英数、記号（`-_.`） |`jig-erd` |
|`jig.erd.output.format` |出力ファイルの形式 |`SVG`, `PNG`, `DOT`(テキスト) |`SVG` |
|`jig.erd.output.rankdir` |ダイアグラムの方向 ([参考](https://graphviz.org/doc/info/attrs.html#d:rankdir)) |`LR`, `RL`, `TB`, `BT` |`LR` |

ファイル名は `{jig.erd.output.prefix}-detail.{拡張子}` などになります。

## リリース

### GitHub Packages

Tagを作るとGitHub Actionsがやります。

### bintray

```
VERSION=0.0.3 ./gradlew bintrayUpload
```

## 未定な予定

- [x] H2database
- [x] Windows
- [x] FKのカラムくらいはだす
- [x] 出力形式や出力先などの設定
- [ ] PostgreSQL
- [x] TABLEのCOMMENTを使う
- [ ] PK/UKのカラムをだす？
- [ ] COLUMNのCOMMENTを使う？
- [ ] SpringBootStarter
- [ ] スタンドアロン
- [ ] jCenter
- [ ] 他のDB

## LICENSE

[Apache License 2.0](LICENSE)
