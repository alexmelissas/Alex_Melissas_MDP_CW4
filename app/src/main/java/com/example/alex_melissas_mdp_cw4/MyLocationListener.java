package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    //Passed in from the service, for BR
    private Context context;

    public MyLocationListener(Context c){
        super();
        context = c;
    }

    // Create new Broadcast with the new location in it
    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(context, MyLocationReceiver.class);
        intent.setAction("com.example.alex_melissas_mdp_cw4.LOCATION_BROADCAST");
        intent.putExtra("location", location);
        context.sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("LocationListener", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LocationListener", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LocationListener", "onProviderDisabled: " + provider);
    }


}
