package com.example.alex_melissas_mdp_cw4;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng pin1, pin2;
    private String whoCalled;
    private String workout_id;

    //is this a map showing current location of ongoing workout?
    //or displaying workout record with start/end points?
    private int currentOrPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        currentOrPast = bundle.getInt("currentOrPast");
        whoCalled = bundle.getString("whoCalled");
        if(currentOrPast==1) workout_id = bundle.getString("workout_id");

        pin1 = bundle.getParcelable("pin1");
        if(currentOrPast == 1) pin2 = bundle.getParcelable("pin2");
        else pin2 = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // HOW TO RECEIVE UPDATES FOR LIVE TRACK?

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Marker marker1 = null;
        Marker marker2 = null;

        if(currentOrPast ==1){
            marker1 = mMap.addMarker(new MarkerOptions()
                            .position(pin1)
                            .title("Start Point")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    );
            marker2 = mMap.addMarker(new MarkerOptions()
                    .position(pin2)
                    .title("End Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

        }else{
            marker1 = mMap.addMarker(new MarkerOptions()
                    .position(pin1)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        if(currentOrPast==1) builder.include(marker2.getPosition());
        LatLngBounds bounds = builder.build();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(pin1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
    }

    public void onClickReturn(View v){
        switch(whoCalled){
            case "SingleWorkout":
                Intent intent = new Intent(MapsActivity.this, SingleWorkout.class);
                Bundle bundle = new Bundle();
                bundle.putString("workout_id",workout_id);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "MainActivity": startActivity(new Intent(MapsActivity.this, MainActivity.class));
            default: startActivity(new Intent(MapsActivity.this, MainActivity.class));
        }

    }

}
