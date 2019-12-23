package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    private Context context;

    public MyLocationListener(Context c){
        super();
        context = c;
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(context, MyLocationReceiver.class);
        intent.setAction("com.example.alex_melissas_mdp_cw4.LOCATION_BROADCAST");
        intent.putExtra("location", location);
        context.sendBroadcast(intent);
    }

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
