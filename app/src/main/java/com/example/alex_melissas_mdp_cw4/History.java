package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History extends AppCompatActivity {

    private String timeFilter;
    private String typeFilter;
    private String sortBy;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTypeFilter(findViewById(R.id.allTypeRadio));
        setTimeFilter(findViewById(R.id.todayRadio));
        setListSort(findViewById(R.id.dateTimeSort));
        readWorkouts(null,null);
    }

    //maybe savestate shit for current projection/selection/args etc?

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onResume() {
        setTypeFilter(findViewById(R.id.allTypeRadio));
        setTimeFilter(findViewById(R.id.todayRadio));
        setListSort(findViewById(R.id.dateTimeSort));
        readWorkouts(null,null);
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readWorkouts(String selection, String[] selectionArgs) {

        final ListView historyList = (ListView) findViewById(R.id.historyList);

        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS,null,
                selection, selectionArgs, "fav DESC, " + sortBy, null);
        String[] columns = new String[]{"dateTime", "duration", "distance", "type", "fav"};
        int[] to = new int[]{R.id.datetimeBox, R.id.durationBox, R.id.distanceBox};
        SimpleCursorAdapter adapter = new WorkoutCursorAdapter(this, R.layout.workout_entry, c, columns, to);
        historyList.setAdapter(adapter);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(History.this, SingleWorkout.class);
                Bundle bundle = new Bundle();

                Cursor c = ((SimpleCursorAdapter)historyList.getAdapter()).getCursor();
                c.moveToPosition(i);
                String workout_id = c.getString(0);
                bundle.putString("workout_id", workout_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    // PUT WITH SAVESTATE SHIT

//    private void updateFromRadios(){
//
//        switch(timeFilter){
//            case "today": ((RadioButton)findViewById(R.id.todayRadio)).setChecked(true); break;
//            case "week": ((RadioButton)findViewById(R.id.weekRadio)).setChecked(true); break;
//            case "month": ((RadioButton)findViewById(R.id.monthRadio)).setChecked(true); break;
//            case "year": ((RadioButton)findViewById(R.id.yearRadio)).setChecked(true); break;
//            case "alltime": ((RadioButton)findViewById(R.id.allTimeRadio)).setChecked(true); break;
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void querySelections(){

        String selection = "";
        if(typeFilter!="-1") {
            selection += "type=?";
            if (timeFilter!="-1")
                selection += " AND " + pickTime();
        }
        else if(timeFilter!="-1"){ selection += pickTime(); }
        else selection = null;

        String splitDate = splitDate();

        String[] selectionArgs = null;
        if(typeFilter!="-1") {
            selectionArgs = new String[]{typeFilter};
            if(timeFilter=="week"){ selectionArgs = new String[]{typeFilter,""+getCurrentWeek()}; }
            else if(timeFilter!="-1") selectionArgs = new String[]{typeFilter,splitDate};
        }
        else if(timeFilter=="week"){ selectionArgs = new String[]{""+getCurrentWeek()}; }
        else if(timeFilter!="-1"){ selectionArgs = new String[]{splitDate}; }

        readWorkouts(selection,selectionArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTimeFilter(View v){
        setTimeFilter(v);
        querySelections();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTypeFilter(View v){
        setTypeFilter(v);
        querySelections();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickListSort(View v){
        setListSort(v);
        querySelections();
    }

    private void setTimeFilter(View v){
        switch(v.getId()){
            case R.id.todayRadio: timeFilter = "today"; break;
            case R.id.weekRadio: timeFilter = "week"; break;
            case R.id.monthRadio: timeFilter = "month"; break;
            case R.id.yearRadio: timeFilter = "year"; break;
            case R.id.allTimeRadio: timeFilter = "-1"; break;
        }
    }

    private void setTypeFilter(View v){
        switch(v.getId()){
            case R.id.allTypeRadio: typeFilter = "-1"; break;
            case R.id.walkRadio: typeFilter = "0"; break;
            case R.id.jogRadio: typeFilter = "1"; break;
            case R.id.runRadio: typeFilter = "2"; break;
        }
    }

    private void setListSort(View v){
        ((TextView)findViewById(R.id.typeSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.dateTimeSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.durationSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.distanceSort)).setTypeface(null, Typeface.NORMAL);

        switch(v.getId()){
            case R.id.typeSort: sortBy = "type DESC";
                ((TextView)findViewById(R.id.typeSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.dateTimeSort: sortBy = "dateTime DESC";
                ((TextView)findViewById(R.id.dateTimeSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.durationSort: sortBy = "duration DESC";
                ((TextView)findViewById(R.id.durationSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.distanceSort: sortBy = "distance DESC";
                ((TextView)findViewById(R.id.distanceSort)).setTypeface(null, Typeface.BOLD);
                break;
        }
    }

    private String pickTime(){
        String selection = "";
        switch(timeFilter){
            case "today": selection += "(SUBSTR(dateTime,1,8))=?"; break;
            case "week": selection += "week=?"; break;
            case "month": selection += "(SUBSTR(dateTime,4,5))=?"; break;
            case "year": selection += "(SUBSTR(dateTime,7,2))=?"; break;
            default: break;
        }
        return selection;
    }

    private String splitDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy | HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        String date = "";

        switch(timeFilter){
            case "today": date = currentDateTime.substring(0,8); break;
            //week is handled separately, see querything
            case "month": date = currentDateTime.substring(3,8); break;
            case "year": date = currentDateTime.substring(6,8); break;
            default: date = currentDateTime.substring(0,8); break;
        }
        return date;
    }

    private int getCurrentWeek(){
            Cursor c = getContentResolver().query(WorkoutsContract.GETCURRRENTWEEK,
                    null,null,null,null);
            c.moveToFirst();
            Log.d("WEEK: ", ""+c.getInt(0));
            return c.getInt(0);
    }

}
