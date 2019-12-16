package com.example.alex_melissas_mdp_cw4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

public class MyLocationService extends Service {

    public LocationManager locationManager;
    public MyLocationListener locationListener;

    public MyLocationService() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
