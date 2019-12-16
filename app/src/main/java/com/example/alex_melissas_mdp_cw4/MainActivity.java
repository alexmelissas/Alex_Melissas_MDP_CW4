package com.example.alex_melissas_mdp_cw4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MyLocationService.MyLocationBinder myLocationBinder = null;
    private String musicFolderPath;
    private Handler progressBarHandler;
    private boolean checkProgress;

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

        checkStoragePermissions(this);
        musicFolderPath = Environment.getExternalStorageDirectory().getPath() + "/Music/";

        this.startService(new Intent(this, MyLocationService.class));
        this.bindService(new Intent(this, MyLocationService.class),
                serviceConnection, BIND_AUTO_CREATE);
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
}
