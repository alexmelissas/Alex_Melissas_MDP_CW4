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


public class WorkoutsProvider extends ContentProvider {

    private DBHelper dbHelper;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "workouts", 1);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "locations", 2);
        uriMatcher.addURI(WorkoutsContract.AUTHORITY, "workoutswithlocations", 3);

        //uriMatcher.addURI(WorkoutsContract.AUTHORITY, "something else", 4);
    }

    @Override
    public boolean onCreate() {
        Log.d("CW4","ContentProvider onCreate");
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

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


        switch(uriMatcher.match(uri)) {
            case 1: return db.query("workouts", projection, selection, selectionArgs, null, null, sortOrder);
            case 2: return db.query("locations", projection, selection, selectionArgs, null, null, sortOrder);
            case 3: return db.query("workoutswithlocations", projection, selection, selectionArgs, null, null, sortOrder);

//            case 4: return db.rawQuery("select r._id as recipe_id, r.name, ri.ingredient_id, i.ingredientname "+
//                            "from recipes r "+
//                            "join recipe_ingredients ri on (r._id = ri.recipe_id)"+
//                            "join ingredients i on (ri.ingredient_id = i._id) where r._id == ?",
//                    selectionArgs);

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

//        {
//            //if deleting recipe then check if nothing else uses an ingredient then delete the ingredient
//            if (tableName == "recipes") {
//                ArrayList<String> ingredients_of_deleted = new ArrayList<>();
//                Cursor c = db.query("recipe_ingredients", new String[]{"recipe_id", "ingredient_id"}, "recipe_id = ?",
//                        selectionArgs, null, null, null);
//
//                if (c.moveToFirst()) {
//                    do {
//                        ingredients_of_deleted.add(c.getString(1));
//                    } while (c.moveToNext());
//                    for (String ing : ingredients_of_deleted) {
//                        Log.d("delete:", "checking rxi for ingredient: " + ing);
//                        db.delete("recipe_ingredients", "recipe_id=?", selectionArgs);
//                        Cursor cc = db.query("recipe_ingredients", new String[]{"ingredient_id"}, "ingredient_id = ?",
//                                new String[]{ing}, null, null, null);
//                        if (!cc.moveToFirst()) db.delete("ingredients", "_id=?", new String[]{ing});
//                        else Log.d("didnt delete ", ing);
//                    }
//                }
//            }
//        }

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