package com.example.alex_melissas_mdp_cw4;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    public Location currentLocation;
    protected float currentDuration;//SEC
    protected float currentDistance;//METERS?
    protected long currentTime;

    public MyLocationListener(){
        super();
        currentLocation=null;
        currentDuration=0;
        currentDistance=0;
        currentTime = System.currentTimeMillis();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocation!=null) currentDistance += currentLocation.distanceTo(location);
        currentLocation = location;

        long newTime = System.currentTimeMillis();
        currentDuration += (newTime - currentTime);
        currentTime = newTime;

        Log.d("MyLocationListener: ", "\nDISTANCE: " + currentDistance/1000 + ", DURATION: "+currentDuration/1000);
    }

    public void reset(){
        currentDuration=0;
        currentDistance=0;
        currentTime = System.currentTimeMillis();
    }

    public Location getLocation(){return currentLocation;};
    public double[] getLocationCoords(){ return new double[]{currentLocation.getLongitude(),currentLocation.getLatitude()}; }
    public float getDistance(){ return currentDistance/1000;}
    public float getDuration(){ return currentDuration/1000;}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { // information about the signal, i.e. number of satellites
        Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
// the user enabled (for example) the GPS Log.d("g53mdp", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
// the user disabled (for example) the GPS Log.d("g53mdp", "onProviderDisabled: " + provider);
    }


}
