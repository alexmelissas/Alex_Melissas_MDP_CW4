package com.example.alex_melissas_mdp_cw4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLocationService extends Service {

    private final IBinder binder = new MyLocationBinder();
    protected LocationManager locationManager;
    protected MyLocationListener locationListener;
    protected MyLocationReceiver locationReceiver;
    RemoteCallbackList<MyLocationBinder> remoteCallbackList = new RemoteCallbackList<MyLocationBinder>();
    NotificationCompat.Builder builder;

    String workout_id;
    boolean workoutActive;
    int workoutType;

    ///////////////////////////////////// G E N E R A L  //////////////////////////////////////////////

    @Override

    // onCreate, also setup the Location: Manager, Listener, and (Broadcast)Receiver.
    public void onCreate() {
        Log.d("¬¬¬¬¬¬¬¬From Service: ", "onCreate'd");
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener(getApplicationContext());
        try { locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 1,locationListener);
        } catch(SecurityException e) { Log.d("location error", e.toString()); }

        locationReceiver = new MyLocationReceiver();
        IntentFilter filter = new IntentFilter("com.example.alex_melissas_mdp_cw4.LOCATION_BROADCAST");
        getApplicationContext().registerReceiver(locationReceiver, filter);

        workoutActive = false;
        super.onCreate();
    }

    //onDestroy, also stop foreground activity, remove notification and deregister the receiver
    public void onDestroy(){
        Log.d("From Service: ","Service dead");
        stopForeground(true);
        IntentFilter filter = new IntentFilter("com.example.alex_melissas_mdp_cw4.LOCATION_BROADCAST");
        getApplicationContext().registerReceiver(locationReceiver, filter);
        super.onDestroy();
    }

    //make service sticky
    public int onStartCommand(Intent intent, int flags, int startId) {return Service.START_STICKY;}

    /////////////////////////////////// C A L L B A C K S  ////////////////////////////////////////////

    // Run the callback to all subscribers and then finish broadcast
    public void doCallBackCheckWorkout(){
        final int n = remoteCallbackList.beginBroadcast();
        Log.d("doCallBack: ","remoteCallList.beginBroadcast n = "+n);
        for (int i=0;i<n;i++){
            remoteCallbackList.getBroadcastItem(i).myLocationCallback.checkWorkout();
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

        //Standard callback registering
        public void registerCallback(MyLocationCallback myLocationCallback) {
            this.myLocationCallback = myLocationCallback;
            remoteCallbackList.register(MyLocationBinder.this);
            Log.d("binder","registered callback");
            doCallBackCheckWorkout();
        }
        //Standard callback unregistering, also if no workout is active => kill service
        public void unregisterCallback(MyLocationCallback myLocationCallback) {
            remoteCallbackList.unregister(MyLocationBinder.this);
            Log.d("binder","unregistered callback");
            if(remoteCallbackList.beginBroadcast()==0) if(!workoutActive) stopSelf();
        }

        public boolean getState(){ if(workoutActive)return true; return false; }

        // Initiate workout
        public long startWorkout(int type) throws ParseException {

            // Only 1 workout allowed at a time
            if(workoutActive) return -1;

            // Error handling - if GPS signal has issues or havent received updates before this method
            // is called then warn user and exit
            if(MyLocationTracker.requestLocationTracker().getLocation() == null) {
                Toast.makeText(getApplicationContext(), "GPS wasn't ready. Please try again." +
                                " If you just allowed the GPS permission, please try relaunching the app.",
                        Toast.LENGTH_LONG).show();
                return -1;
            }
            workoutType = type;



        // 1. Insert new workout, bare-bones for now, just starting date/time & type.

            SimpleDateFormat ddMMyyhhmmss = new SimpleDateFormat("dd/MM/yy | HH:mm:ss",
                    Locale.getDefault());
            String currentDateTime = ddMMyyhhmmss.format(new Date());

            Date ymd = ddMMyyhhmmss.parse(currentDateTime);
            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String currentReverseDate = yyyyMMdd.format(ymd);

            SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
            String currentTime = HHmmss.format(ymd);

            Log.d("dates: ",currentDateTime+", "+currentReverseDate+", "+currentTime);

            ContentValues workoutValues = new ContentValues();
            workoutValues.put(WorkoutsContract.DATETIME, currentDateTime);
            workoutValues.put(WorkoutsContract.YYYYMMDD, currentReverseDate);
            workoutValues.put(WorkoutsContract.HHMMSS, currentTime);
            workoutValues.put(WorkoutsContract.TYPE,workoutType);
            long newWorkoutId = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTS, workoutValues));
            workout_id = "" + newWorkoutId;

        // 2. Insert new WorkoutsWithLocations entry (location as start point of workout),
            // and Location (insert only if new location).

            MyLocationTracker.requestLocationTracker().reset();
            return insertWorkoutWithLocationEntry(0);
        }

        // When user stops workout - get all workout data from MyLocationTracker, store all data in relevant table in db
        public void stopWorkout(){
            float endDistance = MyLocationTracker.requestLocationTracker().getDistance();
            float endDuration = MyLocationTracker.requestLocationTracker().getDuration();

            // Update workout data with final values
            ContentValues finishedWorkout = new ContentValues();
            finishedWorkout.put("duration",endDuration);
            finishedWorkout.put("distance",endDistance);
            finishedWorkout.put("avgSpeed",(endDistance/endDuration)*1000);
            getContentResolver().update(WorkoutsContract.WORKOUTS,finishedWorkout,"_id=?",new String[]{workout_id});

            // Insert new location as end point of workout
            insertWorkoutWithLocationEntry(1);
            workoutActive = false;
            doCallBackCheckWorkout();
        }

        // Insert new entry to WorkoutsWithLocations table, with Workout id and location id, and
        // boolean if location was start or end point of workout. Also start notification process.
        private long insertWorkoutWithLocationEntry(int startStopPoint){

            double lat = MyLocationTracker.requestLocationTracker().getCurrentCoords()[0];
            double lon = MyLocationTracker.requestLocationTracker().getCurrentCoords()[1];

            //Check if this Location already exists
            Cursor c = getContentResolver().query(WorkoutsContract.LOCATIONS,
                    new String[]{WorkoutsContract._ID, WorkoutsContract.LON, WorkoutsContract.LAT},
                    "lon = ? AND lat = ?", new String[]{""+lon,""+lat}, null);

            long returnID;

            // If exists, just create new entry in WorkoutsWithLocations table
            if (c.moveToFirst()) {
                String location_id = "" + c.getInt(0);
                ContentValues workoutsWithLocationsValues = new ContentValues();
                workoutsWithLocationsValues.put(WorkoutsContract.WORKOUT_ID, workout_id);
                workoutsWithLocationsValues.put(WorkoutsContract.LOCATION_ID, location_id);
                workoutsWithLocationsValues.put(WorkoutsContract.STARTSTOPPOINT, startStopPoint);
                workoutActive = true;
                doCallBackCheckWorkout();
                Log.d("Inserting: ","workout id= "+workout_id+", startStop= "+startStopPoint);
                returnID = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTSWITHLOCATIONS, workoutsWithLocationsValues));

                // If not exists, create new entry for it in Locations and then new entry for wID, lID in WwL table
            } else {
                ContentValues locationValues = new ContentValues();
                locationValues.put(WorkoutsContract.LON, lon);
                locationValues.put(WorkoutsContract.LAT, lat);
                long newLocationId = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.LOCATIONS, locationValues));
                String newLocationIdString = "" + newLocationId;

                ContentValues workoutsWithLocationsValues = new ContentValues();
                workoutsWithLocationsValues.put(WorkoutsContract.WORKOUT_ID, workout_id);
                workoutsWithLocationsValues.put(WorkoutsContract.LOCATION_ID, newLocationIdString);
                workoutsWithLocationsValues.put(WorkoutsContract.STARTSTOPPOINT, startStopPoint);
                workoutActive = true;
                doCallBackCheckWorkout();
                Log.d("Inserting: ","workout id= "+workout_id+", startStop= "+startStopPoint);
                returnID = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTSWITHLOCATIONS, workoutsWithLocationsValues));
            }

        // If starting workout, send first notification and initiate the updating of notifications
            if(startStopPoint==0) notification(true);

            return returnID;
        }
    }

    ///////////////////////////////////// N O T I F I C A T I O N S ///////////////////////////////////

    // Create and display a notification while a workout is active, that user can click on and return to
    // the app. Renewable to update workout values.
    public void notification(boolean firstTime){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(firstTime) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "channel name";
                String description = "channel description";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("100", name,
                        importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }

            int NOTIFICATION_ID = 001;
            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            builder = new NotificationCompat.Builder(this , "100")
                    .setSmallIcon(R.mipmap.jog_icon)
                    .setColor(Color.RED)
                    .setContentTitle("FitnessX 2.0")
                    .setContentText(makeNotificationText())
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(NOTIFICATION_ID,builder.build());
            startUpdateNotifications();
        }
        else{
            int NOTIFICATION_ID = 001;
            builder.setContentText(makeNotificationText());
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    // Format a string for the text of the notification with all relevant stats
    private String makeNotificationText(){
        String workout_type = "None";
        switch (workoutType){
            case 0: workout_type="Walk";break;
            case 1: workout_type="Jog";break;
            case 2: workout_type="Run";break;
            default:break;
        }
        String distance = String.format("%02.2f",MyLocationTracker.requestLocationTracker().getDistance())+"km";
        String duration = secToDuration((int)(MyLocationTracker.requestLocationTracker().getDuration()));
        return "In progress: " + workout_type +" | "+duration+" | "+distance;
    }

    // Convert raw seconds int to hh:mm:ss string
    private String secToDuration(int sec){
        int hours = sec/3600;
        int mins = (sec-hours*3600)/60;
        int secs = (sec-hours*3600)%60;
        return String.format("%2d",hours)+":"+String.format("%02d",mins)+":"+String.format("%02d",secs);
    }

    // Handler to keep notification updated with workout progress
    private void startUpdateNotifications(){
        final Handler notificationUpdateHandler = new Handler();
        notificationUpdateHandler.post(new Runnable() {
            @Override
            public void run() {
                if(workoutActive) {
                    notification(false);
                    notificationUpdateHandler.postDelayed(this,500);
                }
            }
        });
    }
}
