package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class History extends AppCompatActivity {

    private String timeFilter;
    private String typeFilter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        readWorkouts(null,null,null, "datetime DESC");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readWorkouts(String[] projection, String selection, String[] selectionArgs, String sortBy) {

        final ListView historyList = (ListView) findViewById(R.id.historyList);

        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS,projection,
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

    public void clickTimeSort(View v){

        switch(v.getId()){
            case R.id.todayRadio: timeFilter = "today";
            case R.id.weekRadio: timeFilter = "week";
            case R.id.monthRadio: timeFilter = "month";
            case R.id.yearRadio: timeFilter = "year";
            case R.id.allTimeRadio: timeFilter = "alltime";
        }

        // make it into a selection and selection arg thing
    }

    public void clickTypeSort(View v){

        switch(v.getId()){
    }

    private void mergeSelections(){
        String selection = 
    }

    public void clickListSort(View v){
        switch()

    }

}
