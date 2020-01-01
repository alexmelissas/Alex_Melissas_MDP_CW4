package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    private MyLocationService.MyLocationBinder myLocationBinder = null;
    protected DBHelper dbHelper;
    protected SQLiteDatabase db;
    protected SimpleCursorAdapter adapter;

///////////////////////////////// P E R M I S S I O N S /////////////////////////////////////////////////////////
    // Check for external storage read access and location access
    public static void checkPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)//NOT ROBUST
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                 Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
///////////////////////////////////// G E N E R A L /////////////////////////////////////////////////////////////

    // Standard onCreate, start and bind the service, create DB, permissions and layout settings.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonVisibility(false);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        checkPermissions(this);

        this.startService(new Intent(this, MyLocationService.class));
        this.bindService(new Intent(this, MyLocationService.class),
                serviceConnection, BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume()
    {
        readRecent();
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(serviceConnection!=null){
            Log.d("main","onDestroy");
            unbindService(serviceConnection);
            myLocationBinder.unregisterCallback(callback);
            serviceConnection=null;
        }
    }

    //Standard ServiceConnection with callbacks
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Alex CW4","Connected");
            myLocationBinder = (MyLocationService.MyLocationBinder) service;
            myLocationBinder.registerCallback(callback);
            if(!myLocationBinder.getState()) readRecent();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Alex CW4","Disconnected");
            myLocationBinder.unregisterCallback(callback);
            myLocationBinder = null;
        }
    };

    // Change MainActivity layout according to if there's an active workout
    private void setButtonVisibility(boolean workoutActive){

        ImageView animationImage = (ImageView)findViewById(R.id.animationImage);
        AnimationDrawable workoutAnimation = (AnimationDrawable) animationImage.getBackground();

        if(workoutActive){
            findViewById(R.id.walkButton).setVisibility(View.GONE);
            findViewById(R.id.jogButton).setVisibility(View.GONE);
            findViewById(R.id.runButton).setVisibility(View.GONE);
            findViewById(R.id.historyButton).setVisibility(View.GONE);
            findViewById(R.id.recordsButton).setVisibility(View.GONE);
            findViewById(R.id.recentText).setVisibility(View.GONE);
            findViewById(R.id.recentList).setVisibility(View.GONE);

            findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
            findViewById(R.id.animationImage).setVisibility(View.VISIBLE);
            workoutAnimation.start();
        }
        else{
            findViewById(R.id.walkButton).setVisibility(View.VISIBLE);
            findViewById(R.id.jogButton).setVisibility(View.VISIBLE);
            findViewById(R.id.runButton).setVisibility(View.VISIBLE);
            findViewById(R.id.historyButton).setVisibility(View.VISIBLE);
            findViewById(R.id.recordsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.recentText).setVisibility(View.VISIBLE);
            findViewById(R.id.recentList).setVisibility(View.VISIBLE);

            findViewById(R.id.stopButton).setVisibility(View.GONE);
            findViewById(R.id.animationImage).setVisibility(View.GONE);
            workoutAnimation.stop();
        }
    }

/////////////////////////// C A L L B A C K  H A N D L I N G ////////////////////////////////////////////////////
    // Callback to know if there is a workout in progress
    MyLocationCallback callback = new MyLocationCallback() {
        @Override
        public void checkWorkout() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(myLocationBinder.getState())setButtonVisibility(true);
                    else setButtonVisibility(false);
                }
            });
        }
    };
/////////////////////////////////// B U T T O N    H A N D L E R S //////////////////////////////////////////////

    // Standard onClick stuff

    public void onClickWalk(View v) throws ParseException { myLocationBinder.startWorkout(0);}
    public void onClickJog(View v) throws ParseException { myLocationBinder.startWorkout(1);}
    public void onClickRun(View v) throws ParseException { myLocationBinder.startWorkout(2);}

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickStop(View v) {myLocationBinder.stopWorkout(); readRecent();}

    public void onClickHistory(View v){startActivity(new Intent(MainActivity.this, History.class));}

    public void onClickRecords(View v){startActivity(new Intent(MainActivity.this, Totals.class));}

    //TEST
    public void onClickTest(View v){startActivity(new Intent(MainActivity.this, MapsActivity.class));}

/////////////////////////////////// D A T A B A S E    S T U F F ////////////////////////////////////////////////

    // Query the 2 most recent workouts
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readRecent() {
        final ListView recentList = (ListView) findViewById(R.id.recentList);
        Cursor c = getContentResolver().query(WorkoutsContract.RECENTS,null,
                null, null, "datetime DESC", null);

        String[] columns = new String[]{"dateTime", "duration", "distance", "type", "fav"};
        int[] to = new int[]{R.id.datetimeBox, R.id.durationBox, R.id.distanceBox};
        adapter = new WorkoutCursorAdapter(this, R.layout.workout_entry, c, columns, to);

        recentList.setAdapter(adapter);

        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, SingleWorkout.class);
                Bundle bundle = new Bundle();

                Cursor c = ((SimpleCursorAdapter)recentList.getAdapter()).getCursor();
                c.moveToPosition(i);
                String workout_id = c.getString(0);
                bundle.putString("workout_id", workout_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
