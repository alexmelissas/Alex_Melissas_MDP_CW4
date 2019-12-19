package com.example.alex_melissas_mdp_cw4;

import android.net.Uri;

public class WorkoutsContract {

    public static final String AUTHORITY = "com.example.alex_melissas_mdp_cw4.WorkoutsProvider";

    public static final Uri WALK = Uri.parse("content://"+AUTHORITY+"/walk");
    public static final Uri JOG = Uri.parse("content://"+AUTHORITY+"/jog");
    public static final Uri RUN = Uri.parse("content://"+AUTHORITY+"/run");
    public static final Uri LOCATIONS = Uri.parse("content://"+AUTHORITY+"/locations");
    public static final Uri ALL = Uri.parse("content://"+AUTHORITY+"/all");

    // HAVE JOINS FOR RECORDS eg. MOST DISTANCE / LONGEST WORKOUTS

    public static final String _ID = "_id";
    public static final String TYPE = "type"; // 1=Walk, 2=Jog, 3=Run
    public static final String NAME = "name";
    public static final String WALK_ID = "walk_id";
    public static final String JOG_ID = "jog_id";
    public static final String RUN_ID = "run_id";

    public static final String WALK_NAME = "walk_name";
    public static final String JOG_NAME = "jog_name";
    public static final String RUN_NAME = "run_name";

    //W/J/R stuff
    public static final String dateTime = "dateTime";
    public static final String startPos = "startPos";
    public static final String endPos = "endPos";
    public static final String distance = "distance";
    public static final String duration = "duration";
    public static final String avgSpeed = "avgSpeed";
    public static final String imgPath = "imgPath";
    public static final String liked = "liked";
    public static final String fav = "fav";

    //Location stuff
    public static final String lon = "lon";
    public static final String lat = "lat";

}
