package com.example.alex_melissas_mdp_cw4;

import android.net.Uri;

public class WorkoutsContract {

    public static final String AUTHORITY = "com.example.alex_melissas_mdp_cw4.WorkoutsProvider";

    public static final Uri WORKOUTS = Uri.parse("content://"+AUTHORITY+"/workouts");
    public static final Uri LOCATIONS = Uri.parse("content://"+AUTHORITY+"/locations");
    public static final Uri WORKOUTSWITHLOCATIONS = Uri.parse("content://"+AUTHORITY+"/workoutswithlocations");

    // HAVE JOINS FOR RECORDS eg. MOST DISTANCE / LONGEST WORKOUTS

    public static final String _ID = "_id";
    public static final String TYPE = "type"; // 0=Walk, 1=Jog, 2=Run
    public static final String NAME = "name";

    //Workout stuff
    public static final String DATETIME = "dateTime";
    public static final String DISTANCE = "distance";
    public static final String DURATION = "duration";
    public static final String AVGSPEED = "avgSpeed";
    public static final String IMGPATH = "imgPath";
    public static final String LIKED = "liked";
    public static final String FAV = "fav";

    //Location stuff
    public static final String LON = "lon";
    public static final String LAT = "lat";
    public static final String STARTSTOPPOINT = "startStopPoint";

    //Workoutwithlocation stuff
    public static final String WORKOUT_ID = "workout_id";
    public static final String LOCATION_ID = "location_id";

}
