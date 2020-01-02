package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History extends AppCompatActivity {

    private String timeFilter;
    private String typeFilter;
    private String sortBy;

    //Standard onCreate, also reset selection radios
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTypeFilter(findViewById(R.id.allTypeRadio));
        setTimeFilter(findViewById(R.id.todayRadio));
        setListSort(findViewById(R.id.dateTimeSort));
        querySelections();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("timeFilter", timeFilter);
        outState.putString("typeFilter", typeFilter);
        outState.putString("sortBy", sortBy);
    }

    // Restore the filters
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        timeFilter = inState.getString("timeFilter");
        typeFilter = inState.getString("typeFilter");
        sortBy = inState.getString("sortBy");

        View timeView = ((RadioButton)findViewById(R.id.todayRadio));
        View  typeView = ((RadioButton)findViewById(R.id.allTypeRadio));
        View sortView = (TextView)findViewById(R.id.dateTimeSort);

        ((RadioButton)findViewById(R.id.todayRadio)).setChecked(true);
        ((RadioButton)findViewById(R.id.allTypeRadio)).setChecked(true);

        switch(timeFilter){
            case "today":
                ((RadioButton)findViewById(R.id.todayRadio)).setChecked(true);
                timeView = ((RadioButton)findViewById(R.id.todayRadio));
                break;
            case "week":
                ((RadioButton)findViewById(R.id.weekRadio)).setChecked(true);
                timeView = ((RadioButton)findViewById(R.id.weekRadio));
                break;
            case "month":
                ((RadioButton)findViewById(R.id.monthRadio)).setChecked(true);
                timeView = ((RadioButton)findViewById(R.id.monthRadio));
                break;
            case "year":
                ((RadioButton)findViewById(R.id.yearRadio)).setChecked(true);
                timeView = ((RadioButton)findViewById(R.id.yearRadio));
                break;
            case "alltime":
                ((RadioButton)findViewById(R.id.allTimeRadio)).setChecked(true);
                timeView = ((RadioButton)findViewById(R.id.allTimeRadio));
                break;
        }

        switch(typeFilter){
            case "-1":
                ((RadioButton)findViewById(R.id.allTypeRadio)).setChecked(true);
                typeView = ((RadioButton)findViewById(R.id.allTypeRadio));
                break;
            case "0":
                ((RadioButton)findViewById(R.id.walkRadio)).setChecked(true);
                typeView = ((RadioButton)findViewById(R.id.walkRadio));
                break;
            case "1":
                ((RadioButton)findViewById(R.id.jogRadio)).setChecked(true);
                typeView = ((RadioButton)findViewById(R.id.jogRadio));
                break;
            case "2":
                ((RadioButton)findViewById(R.id.runRadio)).setChecked(true);
                typeView = ((RadioButton)findViewById(R.id.runRadio));
                break;
        }

        switch(sortBy){
            case "type DESC": sortView = (TextView)findViewById(R.id.typeSort); break;
            case "yyyymmdd DESC, hhmmss DESC": sortView = (TextView)findViewById(R.id.dateTimeSort); break;
            case "duration DESC": sortView = (TextView)findViewById(R.id.durationSort); break;
            case "distance DESC": sortView = (TextView)findViewById(R.id.distanceSort); break;
            case "fav DESC": sortView = (TextView)findViewById(R.id.favSort); break;
        }

        onClickTimeFilter(timeView);
        onClickTypeFilter(typeView);
        onClickListSort(sortView);
        querySelections();
    }

    // Read all necessary data from WORKOUTS table, populate the ListView
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readWorkouts(String selection, String[] selectionArgs) {

        final ListView historyList = (ListView) findViewById(R.id.historyList);

        //add secondary datetime sorting to all other sortings
        if(sortBy!="yyyymmdd DESC, hhmmss DESC") sortBy += ", yyyymmdd DESC, hhmmss DESC";

        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS,null,
                selection, selectionArgs, sortBy, null);
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

    //Shape the query selection and arguments according to selected time/workout type filters
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void querySelections() {

        String selection = "";
        if(typeFilter!="-1") {
            selection += "type=?";
            if (timeFilter!="-1")
                selection += " AND " + pickTime();
        }
        else if(timeFilter!="-1"){ selection += pickTime(); }
        else selection = null;

        String splitDate = formatDate();

        String[] selectionArgs = null;
        if(typeFilter!="-1") {
            selectionArgs = new String[]{typeFilter};
            if(timeFilter!="-1") selectionArgs = new String[]{typeFilter,splitDate};
        }
        else if(timeFilter!="-1"){ selectionArgs = new String[]{splitDate}; }

        readWorkouts(selection,selectionArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickTimeFilter(View v) { setTimeFilter(v); querySelections(); }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickTypeFilter(View v) { setTypeFilter(v); querySelections(); }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickListSort(View v) { setListSort(v); querySelections(); }

    // Set time filter based on selected radio
    private void setTimeFilter(View v){
        switch(v.getId()){
            case R.id.todayRadio: timeFilter = "today"; break;
            case R.id.weekRadio: timeFilter = "week"; break;
            case R.id.monthRadio: timeFilter = "month"; break;
            case R.id.yearRadio: timeFilter = "year"; break;
            case R.id.allTimeRadio: timeFilter = "-1"; break;
        }
    }

    // Set workout type filter based on selected radio
    private void setTypeFilter(View v){
        switch(v.getId()){
            case R.id.allTypeRadio: typeFilter = "-1"; break;
            case R.id.walkRadio: typeFilter = "0"; break;
            case R.id.jogRadio: typeFilter = "1"; break;
            case R.id.runRadio: typeFilter = "2"; break;
        }
    }

    // Shape the sorting of the query
    private void setListSort(View v){
        ((TextView)findViewById(R.id.typeSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.dateTimeSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.durationSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.distanceSort)).setTypeface(null, Typeface.NORMAL);
        ((TextView)findViewById(R.id.favSort)).setTypeface(null, Typeface.NORMAL);

        switch(v.getId()){
            case R.id.typeSort: sortBy = "type DESC";
                ((TextView)findViewById(R.id.typeSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.dateTimeSort: sortBy = "yyyymmdd DESC, hhmmss DESC";
                ((TextView)findViewById(R.id.dateTimeSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.durationSort: sortBy = "duration DESC";
                ((TextView)findViewById(R.id.durationSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.distanceSort: sortBy = "distance DESC";
                ((TextView)findViewById(R.id.distanceSort)).setTypeface(null, Typeface.BOLD);
                break;
            case R.id.favSort: sortBy = "fav DESC";
                ((TextView)findViewById(R.id.favSort)).setTypeface(null, Typeface.BOLD);
                break;
        }
    }

    // Shape the arguments of time selection
    private String pickTime(){
        String selection = "";
        switch(timeFilter){
            case "today": selection += "(SUBSTR(yyyymmdd,1,11))=?"; break;
            case "week": selection += "(strftime('%W', yyyymmdd, 'localtime', 'weekday 0', '-6 days'))=?"; break;
            case "month": selection += "(SUBSTR(yyyymmdd,1,7))=?"; break;
            case "year": selection += "(SUBSTR(yyyymmdd,1,4))=?"; break;
            default: break;
        }
        return selection;
    }

    // Date formatting
    private String formatDate() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String currentReverseDate = yyyyMMdd.format(new Date());
        String date = "";

        switch(timeFilter){
            case "today": date = currentReverseDate.substring(0,10); break;
            case "week": date = ""+getCurrentWeek(); break;
            case "month": date = currentReverseDate.substring(0,7); break;
            case "year": date = currentReverseDate.substring(0,4); break;
            default: date = currentReverseDate.substring(0,10); break;
        }
        return date;
    }

    // Get the current week-of-year
    private int getCurrentWeek(){
            Cursor c = getContentResolver().query(WorkoutsContract.GETCURRRENTWEEK,
                    null,null,null,null);
            c.moveToFirst();
            return c.getInt(0);
    }

}