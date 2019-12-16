package com.example.alex_melissas_mdp_cw4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyLocationService extends Service {

    private final IBinder binder = new MyLocationBinder();
    private final String CHANNEL_ID = "100";
    RemoteCallbackList<MyLocationBinder> remoteCallbackList = new RemoteCallbackList<MyLocationBinder>();
    protected LocationManager locationManager;
    protected MyLocationListener locationListener;


    ///////////////////////////////////// G E N E R A L  //////////////////////////////////////////////

    @Override

    public void onCreate() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        super.onCreate();
        Log.d("¬¬¬¬¬¬¬¬From Service: ", "onCreate'd");
        //notification(); -- when to show?
    }

    public void onDestroy(){
        Log.d("From Service: ","Service dead");
        stopForeground(true);
        super.onDestroy();
    }

    //public int onStartCommand(Intent intent, int flags, int startId) {return Service.START_STICKY;}

    /////////////////////////////////// C A L L B A C K S  ////////////////////////////////////////////

    // Run the callback to all subscribers and then finish broadcast
    public void doCallBackState(){
        final int n = remoteCallbackList.beginBroadcast();
        Log.d("doCallBack: ","remoteCallList.beginBroadcast n = "+n);
        for (int i=0;i<n;i++){
            //remoteCallbackList.getBroadcastItem(i).mp3Callback.checkState();
        }
        remoteCallbackList.finishBroadcast();
    }

    ////////////////////////////////////////// B I N D E R ////////////////////////////////////////////

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("¬¬¬¬¬¬¬¬From Service: ", "binder given");
        return binder;
    }

    public class MyLocationBinder extends Binder implements IInterface {

        MyLocationCallback myLocationCallback;

        @Override
        public IBinder asBinder(){return this;}

        public void registerCallback(MyLocationCallback myLocationCallback) {
            this.myLocationCallback = myLocationCallback;
            remoteCallbackList.register(MyLocationBinder.this);
            Log.d("binder","registered callback");
            doCallBackState();
        }

        public void unregisterCallback(MyLocationCallback myLocationCallback) {
            remoteCallbackList.unregister(MyLocationBinder.this);
            Log.d("binder","unregistered callback");


            if(remoteCallbackList.beginBroadcast()==0){
//                remoteCallbackList.finishBroadcast();
//                if(mp3Player.getState()== MP3Player.MP3PlayerState.ERROR
//                        || mp3Player.getState()==MP3Player.MP3PlayerState.STOPPED) {
//                    stopSelf();
//                }
            }
        }

        public void checkGPS(){
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,5, locationListener);
            }catch(SecurityException e){
                Log.d("alex4",e.toString());
            }
        }

        //float distance = myLocation.distanceTo(someOtherLocation);

    }

    ///////////////////////////////////// N O T I F I C A T I O N S ///////////////////////////////////

    public void notification(){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        //maybe add current distance or steps or whatever

        int NOTIFICATION_ID = 001;
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this , CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alex's Fitness Tracker")
                .setContentText("Click to return to tracker")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(NOTIFICATION_ID,mBuilder.build());
    }
}
