package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "recipesDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE workouts ("
                        + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                        + "type VARCHAR(128) NOT NULL, "
                        + "name VARCHAR(128), "
                        + "dateTime VARCHAR(128) NOT NULL, "
                        + "duration INTEGER, "
                        + "distance FLOAT, "
                        + "avgSpeed INTEGER, "
                        + "imgPath VARCHAR(128), "
                        + "liked INTEGER, "
                        + "fav INTEGER );"
        );

        db.execSQL(
                "CREATE TABLE locations ("
                        + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                        + "lon VARCHAR(128) NOT NULL, "
                        + "lat VARCHAR(128) NOT NULL );"
        );

        db.execSQL(
                "CREATE TABLE workoutswithlocations ("
                        + "location_id INTEGER NOT NULL, "
                        + "workout_id INTEGER NOT NULL, "
                        + "startStopPoint INTEGER NOT NULL, " // boolean 0=start 1=end point of wrkt
                        + "CONSTRAINT fk1 FOREIGN KEY (workout_id) REFERENCES workouts (_id) ON DELETE CASCADE, "
                        + "CONSTRAINT fk2 FOREIGN KEY (location_id) REFERENCES locations (_id) ON DELETE CASCADE, "
                        + "CONSTRAINT _id PRIMARY KEY (workout_id, location_id) );"

        );

        //Test values

//        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('beef stroganoff', 'put it in the microwave\nyou bought it from aldi\ndont act fancy', '3');");
//        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('veal picatta', 'heat it up\nand do some stuff\neat', '2');");
//        db.execSQL("INSERT INTO recipes (name, instructions, rating) VALUES ('salad', 'get out\npick grass\neasy and cheap', '5');");
//
//        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('saffron');");
//        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('veal rib 0.5 kg');");
//        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('beef');");
//        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('milk');");
//        db.execSQL("INSERT INTO ingredients (ingredientname) VALUES ('greens');");
//
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1,3);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (1,4);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (2,1);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (2,2);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (2,4);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (3,1);");
//        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (3,5);");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}