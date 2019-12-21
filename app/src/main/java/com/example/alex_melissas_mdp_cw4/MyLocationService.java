package com.example.alex_melissas_mdp_cw4;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLocationService extends Service {

    private final IBinder binder = new MyLocationBinder();
    private final String CHANNEL_ID = "100";
    protected LocationManager locationManager;
    protected MyLocationListener locationListener;
    RemoteCallbackList<MyLocationBinder> remoteCallbackList = new RemoteCallbackList<MyLocationBinder>();
    boolean workoutActive;
    int workoutType;

    ///////////////////////////////////// G E N E R A L  //////////////////////////////////////////////

    @Override

    public void onCreate() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        workoutActive = false;
        super.onCreate();
        Log.d("¬¬¬¬¬¬¬¬From Service: ", "onCreate'd");

    }

    public void onDestroy(){
        Log.d("From Service: ","Service dead");
        stopForeground(true);
        super.onDestroy();
    }

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

        public void registerCallback(MyLocationCallback myLocationCallback) {
            this.myLocationCallback = myLocationCallback;
            remoteCallbackList.register(MyLocationBinder.this);
            Log.d("binder","registered callback");
            doCallBackCheckWorkout();
        }

        public void unregisterCallback(MyLocationCallback myLocationCallback) {
            remoteCallbackList.unregister(MyLocationBinder.this);
            Log.d("binder","unregistered callback");
            if(remoteCallbackList.beginBroadcast()==0) if(!workoutActive) stopSelf();
        }

        public boolean getState(){ if(workoutActive)return true; return false; }
        //to display current type of activity or smth
        public int getType(){ return workoutType; }

        public void checkGPS(){ // IS THIS WITH BROADCASTS? LIKE GPS UPDATE COME AS BROADCASTS??
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5,5, locationListener);
            }catch(SecurityException e){
                Log.d("alex4",e.toString());
            }
        }

        public long startWorkout(int type) throws ParseException {

            if(workoutActive) return -1;
            workoutType = type;

            notification();
            //-- EG. WHEN SERVICE RUNNING, SHOW CURRENT WORKOUT: TYPE / DURATION / DISTANCE
            // ALSO ABLE TO RETURN TO APP (WHERE YOU CAN STOP WORKOUT / STOP FROM NOTIFICATION EVEN?)

        // 1. Insert new workout, bare-bones for now, just starting date/time & type.

            SimpleDateFormat ddMMyyhhmmss = new SimpleDateFormat("dd/MM/yy | HH:mm:ss", Locale.getDefault());
            String currentDateTime = ddMMyyhhmmss.format(new Date());
            Date ymd = ddMMyyhhmmss.parse(currentDateTime);
            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String currentReverseDate = yyyyMMdd.format(ymd);

            ContentValues workoutValues = new ContentValues();
            workoutValues.put(WorkoutsContract.DATETIME, currentDateTime);
            workoutValues.put(WorkoutsContract.YYYYMMDD, currentReverseDate);
            workoutValues.put(WorkoutsContract.TYPE,workoutType);

            //testing - NEED THINK HOW STORE THESE VALUES - APPEND km and km/h before storing
            workoutValues.put(WorkoutsContract.DURATION,"12:34");
            workoutValues.put(WorkoutsContract.DISTANCE,"10.3");
            workoutValues.put(WorkoutsContract.AVGSPEED,"3.4");

            long newWorkoutId = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTS, workoutValues));
            String newWorkoutIdString = "" + newWorkoutId;

        // 2. Insert new WorkoutsWithLocations entry, and Location if new.

            Location start = null;
            // GET CURRENT LOCATION AS START POINT
            double start_lon = 0;//start.getLongitude();
            double start_lat = 0;//start.getLatitude();

            //Check if this Location already exists
            Cursor c = getContentResolver().query(WorkoutsContract.LOCATIONS,
                    new String[]{WorkoutsContract._ID, WorkoutsContract.LON, WorkoutsContract.LAT},
                    "lon = ? AND lat = ?", new String[]{""+start_lon,""+start_lat}, null);

            // If exists, just create new entry in Recipes x Ingredients table
            if (c.moveToFirst()) {
                String location_id = "" + c.getInt(0);
                Log.d("Location EXISTS: ", location_id);
                ContentValues workoutsWithLocationsValues = new ContentValues();
                workoutsWithLocationsValues.put(WorkoutsContract.WORKOUT_ID, newWorkoutIdString);
                workoutsWithLocationsValues.put(WorkoutsContract.LOCATION_ID, location_id);
                workoutsWithLocationsValues.put(WorkoutsContract.STARTSTOPPOINT, 0);
                workoutActive = true;
                doCallBackCheckWorkout();
                return ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTSWITHLOCATIONS, workoutsWithLocationsValues));

            // If not exists, create new entry for it in Locations and then new entry for wID, lID in WwL table
            } else {

                Log.d("Location: ", "NOT EXIST");

                ContentValues locationValues = new ContentValues();
                locationValues.put(WorkoutsContract.LON, start_lon);
                locationValues.put(WorkoutsContract.LAT, start_lat);
                long newLocationId = ContentUris.parseId(getContentResolver().insert(WorkoutsContract.LOCATIONS, locationValues));
                String newLocationIdString = "" + newLocationId;

                ContentValues workoutsWithLocationsValues = new ContentValues();
                workoutsWithLocationsValues.put(WorkoutsContract.WORKOUT_ID, newWorkoutIdString);
                workoutsWithLocationsValues.put(WorkoutsContract.LOCATION_ID, newLocationIdString);
                workoutsWithLocationsValues.put(WorkoutsContract.STARTSTOPPOINT, 0);
                workoutActive = true;
                doCallBackCheckWorkout();
                return ContentUris.parseId(getContentResolver().insert(WorkoutsContract.WORKOUTSWITHLOCATIONS, workoutsWithLocationsValues));
            }
        }

        public void stopWorkout(){
            workoutActive = false;
            doCallBackCheckWorkout();
        }

        public void runWorkout(){
            // have a thread taking the GPS input etc
            // calculating distance
            // measuring time
            // have variables storing these and then update the locations with final loc and workouts with stats
        }

        public float calculateDistance(Location start, Location end){ return start.distanceTo(end); }
        //keep updating the entry?
        //SHOULD PROBABLY  CALL THIS ALL THE TIME AND ADD TO DISTANCE NOT JUST AT THE END
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

        //EG. WHEN SERVICE RUNNING, SHOW CURRENT WORKOUT: TYPE / DURATION / DISTANCE
        // ALSO ABLE TO RETURN TO APP OR STOP WORKOUT

        int NOTIFICATION_ID = 001;
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this , CHANNEL_ID)
                .setSmallIcon(R.drawable.like_on_icon)
                .setContentTitle("FitnessX 2.0")
                .setContentText("Click to return to tracker")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(NOTIFICATION_ID,mBuilder.build());
    }
}
