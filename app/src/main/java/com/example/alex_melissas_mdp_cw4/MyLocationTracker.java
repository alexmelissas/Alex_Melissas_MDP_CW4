package com.example.alex_melissas_mdp_cw4;


import android.location.Location;
import android.util.Log;

public class MyLocationTracker {

    public Location currentLocation;
    protected float currentDuration;
    protected float currentDistance;
    protected long currentTime;
    private static MyLocationTracker singleton;

    //Singleton constructor
    private MyLocationTracker(){
        currentLocation=null;
        currentDuration=0;
        currentDistance=0;
        currentTime = System.currentTimeMillis();
        singleton = this;
    }

    //Singleton public accessor
    public static MyLocationTracker requestLocationTracker(){
        if(singleton==null) return new MyLocationTracker();
        else return singleton;
    }

    //Singleton destructor
    public static void destroyTracker(){
        singleton=null;
    }

    // Update current location to new one, update workout distance and duration
    public void updateLocation(Location location){
        if(currentLocation!=null) currentDistance += currentLocation.distanceTo(location);
        currentLocation = location;

        long newTime = System.currentTimeMillis();
        currentDuration += (newTime - currentTime);
        currentTime = newTime;

        Log.d("MyLocationTracker: ", "\nDISTANCE: " + currentDistance/1000 + ", DURATION: "+currentDuration/1000);
    }

    // Reset - when starting new workout
    public void reset(){
        currentDuration=0;
        currentDistance=0;
        currentTime = System.currentTimeMillis();
    }

    //Standard getters
    public Location getLocation(){return currentLocation;};
    public double[] getCurrentCoords(){ return new double[]{currentLocation.getLatitude(),currentLocation.getLongitude()}; }
    public float getDistance(){ return currentDistance/1000;}
    public float getDuration(){ return currentDuration/1000;}


}
