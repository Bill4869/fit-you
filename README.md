# hack-me
## 企画書
Team:くろちゃんｓ
## アプリ名：
- とどもん
## コンセプト
現実という名のクソゲーを、みんなハマりがちな育成ゲームに！

（　時間管理　＋　TODO　）×　育成ゲーム
## ターゲット
リモートでの講義により、学校に行く必要がなく、

生活リズムが崩しがちな学生
## これまでの自己管理育成ゲーム
### 多様性がない
- 一つの事柄に特化（脱スマホ中毒、運動促進、睡眠促進）
  - （現実世界で、大切なのは、バランスでは？）
- 育成の結果が、規則的
  - （たまごっちみたいな、育て方で、ランダム性がほしい）
## アイデア絞りでの軸
### 開発の目的
はじめてのモバイルアプリ開発を通して、

楽しくものづくりをするとともに、

次につながる経験を積む
## メンバーの技術レイヤー
- native : kotlin ３人（全員はじめて）
- sever ：rails １人（railstutorial途中）
- UI・design １人
## 機能
### 最低限必要な機能
- TODOリスト
- ステータスによって、進化がかわる育成モンスター
- ログイン
#### db
```
user { id           :int,
       name         :string,
       monster_id   :int, default:1
       level        :int, default:1
       hp           :int, default:10   
       ep           :int, default:0 sum(physical,intelligence,lifestyle,others)
       physical     :int, default:0
       intelligence :int, default:0
       lifestyle    :int, default:0
       others       :int, default:0
     }


todo { id           :int,
       user_id      :int,
       title        :string,
       explanation  :text,
       tag          :string,
       level        :int(将来的には、所要時間の方がよさげ)
       status       :boolean, default:false
     }

monster { id    :int,
          name  :name,
        }
```
## モンスター
目的：ユーザーに「育てたい！！」と思わせる

目標：若者からの受けが良く、「かわいらしさ」と、「ユニークさ」を兼ね備える
## ユーザーレベル
### 各LvUp必要経験値
レベル1 | レベル2 |	レベル3 |	レベル4
------  | ------ | ------- | -------
5 | 10 | 15 | 20
## TODOレベル
- TODO範囲
  - level:1~5
- 獲得経験値:ep
  - ep = level(TODO)
## 進化(今回は省略　Lv.UPで、進化)
### 進化ユーザーレベル(累積)
第一 | 第二 |	第三 |	第四
---- | ---- | --- | -----
5 | 15 | 30 | 50
### 第一進化方向性
#### 特殊進化
- ep =
  - physical
  - intelligence
  - lifestyle
## 欲しい機能（JUST IDEA）
- ログインボーナス
- 日課（朝の３時にリセットさせるTODO）
- 日報（取り組み時間レベルアップ）
- 出産（自分のモンスターと、友達のモンスターと何かすると、卵ゲット）
- ギルド（チームでもタスク管理）
- 進化したモンスターで、バトル？？
- チャレンジ（共通タスクを競う）
- gitのissueと連携？
- WEB版
