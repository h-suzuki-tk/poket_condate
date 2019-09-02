package com.example.sample

fun createIngredientTable () : String {
    return "CREATE TABLE " + DBContract.Ingredient.TABLE_NAME + "(" +
            DBContract.Ingredient.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Ingredient.NAME +" TEXT NOT NULL, " +
            DBContract.Ingredient.SUGAR + " REAL, " +
            DBContract.Ingredient.FAT + " REAL, " +
            DBContract.Ingredient.PROTEIN + " REAL, " +
            DBContract.Ingredient.VITAMIN + " REAL, " +
            DBContract.Ingredient.MINERAL + " REAL, " +
            DBContract.Ingredient.FIBER + " REAL, " +
            DBContract.Ingredient.CALORIE + " REAL, " +
            DBContract.Ingredient.QUANTITY + " REAL, " +
            DBContract.Ingredient.UNIT + " TEXT, " +
            DBContract.Ingredient.ALLERGEN + " INTEGER, " +
            DBContract.Ingredient.CLASS + " INTEGER " +
            ")"
}

fun createFoodTable () : String {
    return "CREATE TABLE " + DBContract.Food.TABLE_NAME + "(" +
            DBContract.Food.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Food.NAME + " TEXT NOT NULL, " +
            DBContract.Food.FAVORITE + " INTEGER, " +
            DBContract.Food.MEMO + " TEXT, " +
            DBContract.Food.CATEGORY + " INTEGER " +
            ")"
}

fun createRecordTable () : String {
    return "CREATE TABLE " + DBContract.Record.TABLE_NAME + "(" +
            DBContract.Record.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Record.FOOD_ID + " INTEGER NOT NULL, " + //FOREIGN KEYも加えること
            DBContract.Record.YEAR + " INTEGER NOT NULL, " +
            DBContract.Record.MONTH + " INTEGER NOT NULL, " +
            DBContract.Record.DATE + " INTEGER NOT NULL, " +
            DBContract.Record.TIME + " INTEGER NOT NULL, " +
            DBContract.Record.NUMBER + " INTEGER NOT NULL " +
            ")"
}

fun createMyCondateTable () : String {
    return "CREATE TABLE " + DBContract.MyCondate.TABLE_NAME  +  "(" +
            DBContract.MyCondate.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.MyCondate.NAME + " TEXT NOT NULL " +
            ")"
}

fun createCategoryTable () : String {
    return "CREATE TABLE " + DBContract.Category.TABLE_NAME  +  "(" +
            DBContract.Category.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Category.NAME + " TEXT NOT NULL, " +
            DBContract.Category.HIGHER_ID + " INTEGER " +
            ")"
}

fun createFoodIngredientsTable () : String {
    return "CREATE TABLE " + DBContract.Foods_Ingredients.TABLE_NAME + "(" +
            DBContract.Foods_Ingredients.FOOD_ID + " INTEGER NOT NULL, " + //FOREIGN KEYも加えること
            DBContract.Foods_Ingredients.INGREDIENT_ID + " INTEGER NOT NULL, " + //FOREIGN KEYも加えること
            DBContract.Foods_Ingredients.NUMBER + " INTEGER NOT NULL " +
            ")"
}

fun createMyCondateFoodTable () : String {
    return "CREATE TABLE " + DBContract.MyCondate_Foods.TABLE_NAME  +  "(" +
            DBContract.MyCondate_Foods.MYCONDATE_ID + " INTEGER NOT NULL, " + //FOREIGN KEYも加えること
            DBContract.MyCondate_Foods.FOOD_ID + " INTEGER NOT NULL, " + //FOREIGN KEYも加えること
            DBContract.MyCondate_Foods.NUMBER + " INTEGER NOT NULL " +
            ")"
}



fun createUserInfoTable () : String {
    return "CREATE TABLE " + DBContract.UserInfo.TABLE_NAME  +  "(" +
            DBContract.UserInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.UserInfo.NAME + " TEXT NOT NULL, " +
            DBContract.UserInfo.HEIGHT + " REAL, " +
            DBContract.UserInfo.WEIGHT + " REAL, " +
            DBContract.UserInfo.AGE + " INTEGER, " +
            DBContract.UserInfo.SEX + " INTEGER " +
            ")"
}

/*テンプレート
fun createTable () : String {
    return "CREATE TABLE " + DBContract..TABLE_NAME  +  "(" +
            DBContract..ID + " INTEGER, " +
            DBContract.. + " , " +

            ");"
}
*/
