package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Totals extends AppCompatActivity {

    private String timeFilter;
    private String typeFilter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totals);

        setTypeFilter(findViewById(R.id.allTypeRadioT));
        setTimeFilter(findViewById(R.id.todayRadioT));
        readCalculateTotals(null,null);
    }

    //maybe savestate shit for current projection/selection/args etc?

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onResume() {
        setTypeFilter(findViewById(R.id.allTypeRadioT));
        setTimeFilter(findViewById(R.id.todayRadioT));
        readCalculateTotals(null,null);
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readCalculateTotals(String selection, String[] selectionArgs) {
        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS,null,
                selection, selectionArgs, null, null);


        int totalSec = 0;
        float totalDis = 0;
        float totalSpeedAvg = 0;
        int i=0;
        if(c.moveToFirst()) do{
            totalSec += durationToSec(c.getString(4));
            totalDis += c.getFloat(5);
            totalSpeedAvg += c.getFloat(6);
            i++;
        }while(c.moveToNext());
        totalSpeedAvg /= i;

        ((TextView)findViewById(R.id.durationTotalText)).setText(secToDuration(totalSec));
        ((TextView)findViewById(R.id.distanceTotalText)).setText(String.format("%.2f",totalDis)+"km");
        ((TextView)findViewById(R.id.speedTotalText)).setText(String.format("%.2f",totalSpeedAvg)+"km/h");
    }

    private int durationToSec(String duration) {
        String[] parts = duration.split(":");
        int mins = Integer.parseInt(parts[0]);
        int secs = Integer.parseInt(parts[1]);
        return (mins*60 + secs);
    }

    private String secToDuration(int sec){
        int hours = sec/3600;
        int mins = (sec-hours*3600)/60;
        int secs = (sec-hours*3600)%60;
        return String.format("%2d",hours)+":"+String.format("%02d",mins)+":"+String.format("%02d",secs);
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

        readCalculateTotals(selection,selectionArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTimeFilter(View v) { setTimeFilter(v); querySelections(); }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickTypeFilter(View v) { setTypeFilter(v); querySelections(); }

    private void setTimeFilter(View v){
        switch(v.getId()){
            case R.id.todayRadioT: timeFilter = "today"; break;
            case R.id.weekRadioT: timeFilter = "week"; break;
            case R.id.monthRadioT: timeFilter = "month"; break;
            case R.id.yearRadioT: timeFilter = "year"; break;
            case R.id.allTimeRadioT: timeFilter = "-1"; break;
        }
    }

    private void setTypeFilter(View v){
        switch(v.getId()){
            case R.id.allTypeRadioT: typeFilter = "-1"; break;
            case R.id.walkRadioT: typeFilter = "0"; break;
            case R.id.jogRadioT: typeFilter = "1"; break;
            case R.id.runRadioT: typeFilter = "2"; break;
        }
    }

    //make the time selection part
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

    //split date for
    private String formatDate() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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

    private int getCurrentWeek(){
        Cursor c = getContentResolver().query(WorkoutsContract.GETCURRRENTWEEK,
                null,null,null,null);
        c.moveToFirst();
        return c.getInt(0);
    }

}
