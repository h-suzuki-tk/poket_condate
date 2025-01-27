package com.example.sample

val default = listOf(
    Table.Ingredient("お米", 38.1f, 0.3f, 3.5f, 0f, 0f,
        0f, 168f, 100f, "グラム", 0, 0),
    Table.Ingredient("いくら", 0.12f, 9.36f, 19.56f, null, 0f,
        0f, 163f, 60f, "グラム", 0, 0),
    Table.Ingredient("大根", 3.3f,	2.2f,	0.1f,	null,	null,
        8.0f,	25f,	1f,	  "本",	0, 0),
    Table.Ingredient("ぶり", 0.6f,	35.2f,	42.8f,	null,	null,
    1.2f,	514f,	200f,	  "グラム",	0, 0),
    Table.Ingredient("唐揚げ弁当", 84.36f,	33.36f,	24.24f,	null,	null,
    2.56f,	760f,	1f,	  "個",	0, 1),
    /* 6～ */
    Table.Ingredient("ラーメン二郎", 165f,	93f,	55f,	null,	null,
    3.9f,	1720f,	1f,	  "個",	0, 1),
    Table.Ingredient("パスタ(茹で後)", 71f,	2.25f,	13f,	null,	null,
    3.75f,	373f,	250f,	  "グラム",	0, 0),
    Table.Ingredient("オリーブオイル", 0f,	12f,	0f,	null,	null,
    0f,	111f,	1f,	  "おおさじ",	1, 0),
    Table.Ingredient("さんま", 0.07f,	17f,	12.77f,	null,	null,
        0f,	214f,	1f,	  "尾",	1, 0),
    Table.Ingredient("にんにく", 1.58f,	0.08f,	0.36f,	null,	null,
    8.0f,	8f,	1f,	  "片",	0, 0),
    /* 11～ */
    Table.Ingredient("塩", 0f,	0f,	0f,	null,	null,
        0f,	0f,	10f,	  "おおさじ",	1, 0),
    Table.Ingredient("キムチ", 7.9f,	0.3f,	2.8f,	null,	null,
    2.7f,	46f,	100f,	  "グラム",	1, 0),
    Table.Ingredient("もやし", 2.6f,	0.1f,	1.7f,	null,	null,
        1.3f,	14f,	100f,	  "グラム",	1, 0),
    Table.Ingredient("にら", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	0.5f,	  "束",	1, 0),
    Table.Ingredient("卵", 0.18f, 6.18f, 7.38f, null, null,
        0f, 91f, 1f, "個", 0, 0),
    /* 16～ */
    Table.Ingredient("鶏もも肉", 0f, 3.9f, 18.8f, null, null,
        0f, 116f, 100f, "グラム", 0, 0),
    Table.Ingredient("めんつゆ", 8.7f, 0f, 4.4f, null, null,
        0f, 44f, 100f, "グラム", 0, 0),
    Table.Ingredient("玉ねぎ", 12.75f, 0.18f, 1.77f, null, null,
        2.83f, 65f, 1f, "個", 0, 0),
    Table.Ingredient("ごぼうサラダ", 14.85f, 10.32f, 2.56f, null, null,
        4.3f, 158f, 100f, "グラム", 0, 1),
    Table.Ingredient("乾燥わかめ", 4.13f, 0.16f, 1.36f, null, null,
        3.27f, 12f, 10f, "グラム", 0, 0),
    /* 21～ */
    Table.Ingredient("味噌", 6.82f, 0.54f, 1.75f, null, null,
        1.01f, 39f, 1f, "大さじ", 0, 0),
    Table.Ingredient("絹ごし豆腐", 2f, 3f, 4.9f, null, null,
        0.3f, 56f, 100f, "グラム", 0, 0),

    Table.Food_Ingredient(1, 1, 1.0f),
    Table.Food_Ingredient(1, 2, 1.0f),
    Table.Food_Ingredient(2, 3, 2.0f),
    Table.Food_Ingredient(2, 4, 1.0f),
    Table.Food_Ingredient(3, 5, 1.0f),
    Table.Food_Ingredient(4, 6, 1.0f),
    Table.Food_Ingredient(6, 7, 1.0f),
    Table.Food_Ingredient(6, 8, 1.0f),
    Table.Food_Ingredient(6, 10, 1.0f),
    Table.Food_Ingredient(7, 9, 1.0f),
    Table.Food_Ingredient(7, 11, 1.0f),
    Table.Food_Ingredient(7, 3, 1.0f),
    Table.Food_Ingredient(5, 12, 1.0f),
    Table.Food_Ingredient(5, 13, 1.0f),
    Table.Food_Ingredient(5, 14, 1.0f),
    Table.Food_Ingredient(8, 1, 1.0f),
    Table.Food_Ingredient(9, 1, 2.0f),
    Table.Food_Ingredient(9, 15, 1.0f),
    Table.Food_Ingredient(9, 16, 2.0f),
    Table.Food_Ingredient(9, 17, 1.0f),
    Table.Food_Ingredient(9, 18, 0.5f),
    Table.Food_Ingredient(10, 19, 1.2f),
    Table.Food_Ingredient(11, 20, 1.0f),
    Table.Food_Ingredient(11, 21, 1.0f),
    Table.Food_Ingredient(11, 22, 0.5f),
    Table.Food_Ingredient(12,1,2.0f),
    Table.Food_Ingredient(12,15,1.0f),
    Table.Food_Ingredient(13,2108,1.0f),
    Table.Food_Ingredient(14,15,1f),
    Table.Food_Ingredient(14,2109,1.5f),
    Table.Food_Ingredient(14,2110,0.5f),
    Table.Food_Ingredient(15,2112,0.15f),
    Table.Food_Ingredient(15,2111,1.0f),
    Table.Food_Ingredient(15,13,1.0f),
    Table.Food_Ingredient(16,2113,1.0f),
    Table.Food_Ingredient(17, 1613, 70f),
    Table.Food_Ingredient(17, 397, 25f),
    Table.Food_Ingredient(17, 453, 25f),
    Table.Food_Ingredient(17, 200, 10f),
    Table.Food_Ingredient(17, 668, 25f),
    Table.Food_Ingredient(18, 293, 100f),
    Table.Food_Ingredient(18, 1192, 10f),
    Table.Food_Ingredient(18, 200, 5f),
    Table.Food_Ingredient(18, 1205, 5f),
    Table.Food_Ingredient(18, 230, 10f),
    Table.Food_Ingredient(19, 143, 70f),
    Table.Food_Ingredient(19, 908, 10f),
    Table.Food_Ingredient(19, 1225, 7.5f),
    Table.Food_Ingredient(19, 1258, 22.5f),
    Table.Food_Ingredient(19, 1192, 2.5f),
    Table.Food_Ingredient(19, 1199, 0.5f),
    Table.Food_Ingredient(19, 1289, 0.5f),
    Table.Food_Ingredient(20, 840, 150f),
    Table.Food_Ingredient(20, 349, 5f),
    Table.Food_Ingredient(20, 200, 2.5f),
    Table.Food_Ingredient(20, 1126, 7.5f),
    Table.Food_Ingredient(20, 1192, 15f),
    Table.Food_Ingredient(20, 1150, 15f),
    Table.Food_Ingredient(21, 701, 75f),
    Table.Food_Ingredient(21, 465, 45f),
    Table.Food_Ingredient(21, 142, 50f),
    Table.Food_Ingredient(21, 624, 75f),
    Table.Food_Ingredient(21, 1521, 30f),
    Table.Food_Ingredient(21, 432, 50f),
    Table.Food_Ingredient(21, 345, 25f),
    Table.Food_Ingredient(21, 683, 3.75f),
    Table.Food_Ingredient(21, 2034, 1f),
    Table.Food_Ingredient(21, 1192, 25f),
    Table.Food_Ingredient(21, 1150, 25f),
    Table.Food_Ingredient(21, 200, 10f),

    Table.Food("いくらご飯", 1, null, 8),
    Table.Food("ぶり大根", 0, "シンプルな調理", 4),
    Table.Food("唐揚げ弁当", 1, "ファミマのやつ", 7),
    Table.Food("二郎ラーメン", 0, "普通盛り", 5),
    Table.Food("キムチ炒め", 0, null, 4),
    /* 6～ */
    Table.Food("ペペロンチーノ", 1, null, 6),
    Table.Food("さんまの塩焼き", 0, "秋の味覚", 4),
    Table.Food("白米", 0, "普通のご飯", 1),
    Table.Food("親子丼", 1, null, 8),
    Table.Food("ごぼうサラダ(小鉢)", 1, "作り置き", 4),
    /* 11～ */
    Table.Food("味噌汁", 1, "1杯", 1),
    Table.Food("卵かけご飯",0,null,1),
    Table.Food("ビッグマック",0,null,2),
    Table.Food("ホットケーキ",0,null,2),
    Table.Food("もやし炒め",0,null,1),
    /* 16~ */
    Table.Food("丸亀釜揚げうどん",0,null,1),
    Table.Food("あじの南蛮漬け", 0, "【1】玉ねぎは薄切りにする。にんじんは千切りにする。保存容器に酢、砂糖、醤油、塩、和風だしの素を入れよく混ぜて、玉ねぎ、にんじんを入れて混ぜ、なじませておく。\n【2】玉ねぎは薄切りにする。にんじんは千切りにする。保存容器に酢、砂糖、醤油、塩、和風だしの素を入れよく混ぜて、玉ねぎ、にんじんを入れて混ぜ、なじませておく。\n【3】小骨を取ったアジを食べやすい大きさに切り、片栗粉をまぶす。\n【4】フライパンに油を入れて熱し、アジを入れて上下に返しながら中火で揚げ焼きにする。\n【5】1に3を入れて、10分以上漬け込む。", 1),
    Table.Food("かぼちゃの甘辛焼き", 1, null, 2),
    Table.Food("サツマイモとベーコンのオイマヨ炒め", 0, null, 2),
    Table.Food("しょうが焼き", 1, "【1】肉を軽く焼く。\n【2】しょうが、砂糖、酒、しょうゆ、みりんを入れ、たれがほとんどなくなれば完成。", 1),
    /* 21~ */
    Table.Food("すき焼き", 1, null, 1),

    Table.Category("和食", 0),
    Table.Category("洋食", 0),
    Table.Category("中華", 0),
    Table.Category("惣菜", 1),
    Table.Category("ラーメン", 3),
    Table.Category("パスタ", 2),
    Table.Category("弁当", 1),
    Table.Category("丼", 1),


    Table.MyCondate("つまみセット"),
    Table.MyCondate("部活帰り"),
    Table.MyCondate("いつもの"),


    Table.MyCondate_Food(1, 2, 1.0f),
    Table.MyCondate_Food(1, 7, 1.0f),
    Table.MyCondate_Food(2, 3, 1.0f),
    Table.MyCondate_Food(1, 5, 2.0f),
    Table.MyCondate_Food(3, 1, 1.0f),
    Table.MyCondate_Food(1, 7, 1.0f),


    Table.Record(1, 2019, 7, 13, 1, 1f),
    Table.Record(6, 2019, 7, 13, 2, 1.5f),
    Table.Record(7, 2019, 7, 14, 0, 2f),
    Table.Record(8, 2019, 7, 14, 0, 1f),
    Table.Record(4, 2019, 7, 14, 1, 0.5f),
    Table.Record(3, 2019, 7, 14, 2, 1f),


    Table.UserInfo("かじむら大明神", 195f, 75.7f, 16, 1)
)

val default_after = listOf(

    /* 2108~ */
    Table.Ingredient("ビッグマック", 41.8f, 28.3f, 26.0f, null, null,
        2.6f, 56f, 217f, "個", 0, 1),
    Table.Ingredient("ホットケーキミックス",80.4f,2.7f,7.2f,null,null,
        0.0f,375f,100f,"グラム",1,0),
    Table.Ingredient("ホットケーキシロップ",30.2f,0f,0f,null,null,
        0.0f,121f,41f,"グラム",1,0),
    /* 2111~ */
    Table.Ingredient("サラダ油",0.0f,14f,0.0f,null,null,
        0.0f,126f,14f,"グラム",1,0),
    Table.Ingredient("醤油",4.93f,0.57f,8.14f,null,null,
        0.8f,53.0f,100f,"グラム",1,0),
    Table.Ingredient("丸亀釜揚げうどん",70.7f,1.5f,10.4f,null,null,
        0.3f,338f,270f,"杯",1,1)



)