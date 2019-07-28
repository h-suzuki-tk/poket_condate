package com.example.sample

//とりあえず仮に入れたデータです。
//栄養分はコピペ乱用で適当です、すみません。
val default = listOf(
    Table.Ingredient("お米", 38.1f, 0.3f, 3.5f, 0f, 0f,
        0f, 168f, 100f, "グラム", 0),
    Table.Ingredient("いくら", 0.12f, 9.36f, 19.56f, null, 0f,
        null, 163f, 60f, "グラム", 1),
    Table.Ingredient("大根", 3.3f,	2.2f,	0.1f,	null,	null,
        8.0f,	25f,	1f,	  "本",	1),
    Table.Ingredient("ぶり", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	25f,	100f,	  "グラム",	0),
    Table.Ingredient("唐揚げ弁当", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	732f,	1f,	  "個",	1),
    Table.Ingredient("ラーメン二郎", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	1f,	  "個",	1),
    Table.Ingredient("パスタ(茹で後)", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	200f,	  "グラム",	1),
    Table.Ingredient("オリーブオイル", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	1f,	  "おおさじ",	1),
    Table.Ingredient("さんま", 3.3f,	2.2f,	0.1f,	null,	null,
        8.0f,	1268f,	1f,	  "尾",	1),
    Table.Ingredient("にんにく", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	1f,	  "片",	1),
    Table.Ingredient("塩", 3.3f,	2.2f,	0.1f,	null,	null,
        8.0f,	1268f,	10f,	  "グラム",	1),
    Table.Ingredient("キムチ", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	100f,	  "グラム",	1),
    Table.Ingredient("もやし", 3.3f,	2.2f,	0.1f,	null,	null,
        8.0f,	1268f,	100f,	  "グラム",	1),
    Table.Ingredient("にら", 3.3f,	2.2f,	0.1f,	null,	null,
    8.0f,	1268f,	0.5f,	  "束",	1),


    Table.Food_Ingredient(1, 1, 1),
    Table.Food_Ingredient(1, 2, 1),
    Table.Food_Ingredient(2, 3, 2),
    Table.Food_Ingredient(2, 4, 1),
    Table.Food_Ingredient(3, 5, 1),
    Table.Food_Ingredient(6, 7, 1),
    Table.Food_Ingredient(6, 8, 1),
    Table.Food_Ingredient(6, 10, 1),
    Table.Food_Ingredient(7, 9, 1),
    Table.Food_Ingredient(7, 11, 1),
    Table.Food_Ingredient(7, 3, 1),
    Table.Food_Ingredient(5, 12, 1),
    Table.Food_Ingredient(5, 13, 1),
    Table.Food_Ingredient(5, 14, 1),
    Table.Food_Ingredient(8, 1, 1),


    Table.Food("いくらご飯", 1, null, 8),
    Table.Food("ぶり大根", 0, "シンプルな調理", 4),
    Table.Food("唐揚げ弁当", 1, "ファミマのやつ", 7),
    Table.Food("二郎ラーメン", 0, "普通盛り", 5),
    Table.Food("キムチ炒め", 0, null, 4),
    Table.Food("ペペロンチーノ", 1, null, 6),
    Table.Food("さんまの塩焼き", 0, "秋の味覚", 4),
    Table.Food("白米", 0, "普通のご飯", 1),


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


    Table.MyCondate_Food(1, 2, 1),
    Table.MyCondate_Food(1, 7, 1),
    Table.MyCondate_Food(2, 3, 1),
    Table.MyCondate_Food(1, 5, 2),
    Table.MyCondate_Food(3, 1, 1),
    Table.MyCondate_Food(1, 7, 1),


    Table.Record(1, 2019, 7, 13, 1),
    Table.Record(6, 2019, 7, 13, 2),
    Table.Record(7, 2019, 7, 14, 0),
    Table.Record(8, 2019, 7, 14, 0),
    Table.Record(4, 2019, 7, 14, 1),
    Table.Record(3, 2019, 7, 14, 2),


    Table.UserInfo("かじむら大明神", 195f, 75.7f, 16, 1)
)