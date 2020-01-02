package com.example.alex_melissas_mdp_cw4;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class WorkoutsProvider extends ContentProvider {

    private DBHelper dbHelper;
    private static final UriMatcher uriMatcher;

    //URI matching to codes
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "workouts", 1);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "locations", 2);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "workoutswithlocations", 3);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "recents", 4);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "getcurrentweek", 5);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "locationsofworkout", 6);
    }

    @Override
    public boolean onCreate() {
        Log.d("CW4","ContentProvider onCreate");
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    // Helper method to get table name from Uri match
    private String getTableFromUri(@Nullable Uri uri){
        switch(uriMatcher.match(uri)){
            case 1: return "workouts";
            case 2: return "locations";
            case 3: return "workoutswithlocations";
            default: return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if(uri.getLastPathSegment()==null) return "vnd.android.cursor.dir/WorkoutsProvider.data.text";
        else return "vnd.android.cursor.item/WorkoutsProvider.data.text";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d("QUERY: ",getTableFromUri(uri) + " | " + selection  + " | " + sortOrder);

        switch(uriMatcher.match(uri)) {
            case 1: return db.query("workouts", projection, selection, selectionArgs, null, null, sortOrder);
            case 2: return db.query("locations", projection, selection, selectionArgs, null, null, sortOrder);
            case 3: return db.query("workoutswithlocations", projection, selection, selectionArgs, null, null, sortOrder);
            case 4: return db.rawQuery("SELECT * FROM workouts ORDER BY yyyymmdd DESC, hhmmss DESC LIMIT 2",selectionArgs);
            case 5: return db.rawQuery("SELECT strftime('%W', 'now', 'localtime', 'weekday 0', '-6 days') FROM workouts;",null);
            case 6: return db.rawQuery("select l.lat, l.lon, wl.startStopPoint, wl.workout_id "+
                            "from workouts w "+
                            "join workoutswithlocations wl on (w._id = wl.workout_id) "+
                            "join locations l on (wl.location_id = l._id) where w._id == ?",
                    selectionArgs);
            default: return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = getTableFromUri(uri);
        long id = db.insert(tableName,null,values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri,id);
        Log.d("CW4",nu.toString());
        getContext().getContentResolver().notifyChange(nu,null);
        return nu;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = getTableFromUri(uri);

        //if deleting workout then check if nothing else uses a location => if so, delete location
        if (tableName == "workouts") {
            ArrayList<String> ids_of_locations = new ArrayList<>();
            Cursor c = db.query("workoutswithlocations", new String[]{"workout_id", "location_id"}, "workout_id = ?",
                    selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                do {
                    ids_of_locations.add(c.getString(1));
                } while (c.moveToNext());
                for (String id : ids_of_locations) {
                    Log.d("delete:", "checking wwl for location: " + id);
                    db.delete("workoutswithlocations", "workout_id=?", selectionArgs);
                    Cursor cc = db.query("workoutswithlocations", new String[]{"location_id"}, "location_id = ?",
                            new String[]{id}, null, null, null);
                    if (!cc.moveToFirst()) db.delete("locations", "_id=?", new String[]{id});
                    else Log.d("didnt delete ", id);
                }
            }
        }

        db.delete(tableName,selection,selectionArgs);
        db.close();
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = getTableFromUri(uri);
        db.update(tableName,values,selection,selectionArgs);
        db.close();
        return 0;
    }


}