package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MyLocationService.MyLocationBinder myLocationBinder = null;
    private String musicFolderPath;
    private Handler progressBarHandler;
    private boolean checkProgress;

    protected DBHelper dbHelper;
    protected SQLiteDatabase db;
    protected SimpleCursorAdapter adapter;
    String sortBy = "datetime";


    //Standard ServiceConnection with callbacks
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("Alex CW2","Connected");
            myLocationBinder = (MyLocationService.MyLocationBinder) service;
            myLocationBinder.registerCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("Alex CW2","Disconnected");
            myLocationBinder.unregisterCallback(callback);
            myLocationBinder = null;
        }
    };

    /////////////////////////// C A L L B A C K  H A N D L I N G ///////////////////////////////////////
    MyLocationCallback callback = new MyLocationCallback() {
//        @Override
//        public void checkState() {
//            runOnUiThread(new Runnable() {
//                // Update now playing title, progress bar according to if something's playing/paused
//                // or not.
//                @Override
//                public void run() {
//                    //displaySongTitles();
//                    if(myLocationBinder.getState()== MP3Player.MP3PlayerState.PLAYING
//                            || myLocationBinder.getState()== MP3Player.MP3PlayerState.PAUSED){
//                        checkProgress = true;
//                        initiateProgressUpdating();
//                        ((TextView)findViewById(R.id.currentSongText))
//                                .setText(pathToTitle(myLocationBinder.getFilePath()));
//                    }
//                    else {
//                        ((TextView)findViewById(R.id.currentSongText)).setText("Nothing Playing.");
//                        ((SeekBar)findViewById(R.id.progressBar)).setProgress(0);
//                        ((TextView)findViewById(R.id.elapsedTime)).setText("0:00");
//                    }
//                }
//            });
//        }
    };

    /////////////////////////// S T O R A G E   P E R M I S S I O N S ///////////////////////////////////
    public static void checkStoragePermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    ///////////////////////////////////// G E N E R A L /////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        checkStoragePermissions(this);
        musicFolderPath = Environment.getExternalStorageDirectory().getPath() + "/Music/";

        this.startService(new Intent(this, MyLocationService.class));
        this.bindService(new Intent(this, MyLocationService.class),
                serviceConnection, BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume()
    {
//        if(sortBy == "name") ((RadioButton)findViewById(R.id.nameRadio)).setChecked(true);
//        else ((RadioButton)findViewById(R.id.ratingRadio)).setChecked(true);
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

    /////////////////////////////////// B U T T O N    H A N D L E R S //////////////////////////////////


    /////////////////////////////////// D A T A B A S E    S T U F F ////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void readRecent() {
        final ListView recentList = (ListView) findViewById(R.id.recentList);
        Cursor c = getContentResolver().query(WorkoutsContract.ALL,
                new String[]{"w_id", "type", "dateTime", "duration", "distance"},
                null, null, sortBy, null);
        String[] columns = new String[]{"dateTime", "duration", "distance","w_id"};

        // how to switch image for type of each entry??

        int[] to = new int[]{R.id.datetimeBox, R.id.distanceBox, R.id.durationBox};
        adapter = new SimpleCursorAdapter(this, R.layout.workout_entry, c, columns, to, 0);
        recentList.setAdapter(adapter);

        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, SingleWorkout.class);
                Bundle bundle = new Bundle();

                Cursor c = ((SimpleCursorAdapter)recentList.getAdapter()).getCursor();
                c.moveToPosition(i);
                String recipe_id = c.getString(0);

                bundle.putString("recipe_id", recipe_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
