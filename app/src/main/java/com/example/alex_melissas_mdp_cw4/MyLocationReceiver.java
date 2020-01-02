package com.example.alex_melissas_mdp_cw4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class MyLocationReceiver extends BroadcastReceiver {

    @Override
    //Handle receiving new location from LocationListener
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Location location = (Location) data.getParcelable("location");

        //Send it to LocationTracker (Singleton) for measuring stats
        MyLocationTracker.requestLocationTracker().updateLocation(location);
    }

}
