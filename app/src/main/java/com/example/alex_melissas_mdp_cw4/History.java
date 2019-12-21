package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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
        sortBy = "datetime DESC";
        readWorkouts(null,null);
    }

    //maybe savestate shit for current projection/selection/args etc?

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onResume(Bundle savedInstanceState) {
        super.onResume();
        readWorkouts(null,null);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTimeSort(View v){

        switch(v.getId()){
            case R.id.todayRadio: timeFilter = "today"; break;
            case R.id.weekRadio: timeFilter = "week"; break;
            case R.id.monthRadio: timeFilter = "month"; break;
            case R.id.yearRadio: timeFilter = "year"; break;
            case R.id.allTimeRadio: timeFilter = "-1"; break;
        }
        querySelections();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTypeSort(View v){

        switch(v.getId()){
            case R.id.allTypeRadio: typeFilter = "-1"; break;
            case R.id.walkRadio: typeFilter = "0"; break;
            case R.id.jogRadio: typeFilter = "1"; break;
            case R.id.runRadio: typeFilter = "2"; break;
        }
        querySelections();
    }

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

        //need way to get current month etc
        // eg. today -match all 3 dd/MM/yy / week match last 2 / year match last

        String splitDate = splitDate();
        Log.d("spltidate: ",splitDate);
        String[] selectionArgs = null;
        if(typeFilter!="-1") {
            selectionArgs = new String[]{typeFilter};
            if(timeFilter!="-1") selectionArgs = new String[]{typeFilter,splitDate}; //WORKS FOR TODAY ONLY LOL
        }
        else if(timeFilter!="-1"){ selectionArgs = new String[]{splitDate}; }

        readWorkouts(selection,selectionArgs);
    }

    private String pickTime(){
        String selection = "";
        switch(timeFilter){
            case "today": selection += "(SUBSTR(dateTime,1,7))=?"; break;
            //case "week": selection += "SUBSTR(dateTime,1,7)=?"; break;
            case "month": selection += "(SUBSTR(dateTime,4,4))=?"; break;
            case "year": selection += "(SUBSTR(dateTime,6,1))=?"; break;
            default: break;
        }
        Log.d("selection: ",selection);
        return selection;
    }

    private String splitDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy | HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        String date = "";

        switch(timeFilter){
            case "today": date = currentDateTime.substring(0,8); break;
            //case "week": selection += "SUBSTR(dateTime,1,7) = ?"; break;
            case "month": date = currentDateTime.substring(3,8); break;
            case "year": date = currentDateTime.substring(6,8); break;
            default: date = currentDateTime.substring(0,8); break;
        }
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickListSort(View v){

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
        querySelections();
    }

}
