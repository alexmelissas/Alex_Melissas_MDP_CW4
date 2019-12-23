package com.example.alex_melissas_mdp_cw4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class MyLocationReceiver extends BroadcastReceiver {

    @Override
    // Send the new Location to the LocationTracker class, to measure stats
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Location location = (Location) data.getParcelable("location");
        MyLocationTracker.requestLocationTracker().updateLocation(location);
    }

}
