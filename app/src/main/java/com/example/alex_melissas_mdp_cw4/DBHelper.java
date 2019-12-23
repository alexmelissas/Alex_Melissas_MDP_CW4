package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "fitnessDB", null, 1);
    }

    // Create tables needed
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE workouts ("
                        + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                        + "type VARCHAR(128) NOT NULL, "
                        + "name VARCHAR(128), "
                        + "dateTime VARCHAR(128) NOT NULL, "
                        + "duration INTEGER, "
                        + "distance FLOAT, "
                        + "avgSpeed FLOAT, "
                        + "image BLOB, "
                        + "liked INTEGER, "
                        + "fav INTEGER, "
                        + "notes TEXT, "
                        + "yyyymmdd VARCHAR(10), " +
                        "hhmmss VARCHAR(10));"

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
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}