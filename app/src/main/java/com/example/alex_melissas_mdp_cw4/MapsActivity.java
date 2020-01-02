package com.example.alex_melissas_mdp_cw4;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

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

    private GoogleMap googleMap;
    private LatLng pin1, pin2;

    //is this a map showing current location of ongoing workout?
    //or displaying workout record with start/end points?
    private int currentOrPast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        currentOrPast = bundle.getInt("currentOrPast");

        pin1 = bundle.getParcelable("pin1");
        if(currentOrPast == 1) pin2 = bundle.getParcelable("pin2");
        else pin2 = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Add the start marker and current/end marker to the map and display it. Also adjust zoom to fit markers.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Marker marker1 = null;
        Marker marker2 = null;

        marker1 = this.googleMap.addMarker(new MarkerOptions()
                .position(pin1)
                .title("Start Point")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );

        if(currentOrPast ==1){
            marker2 = this.googleMap.addMarker(new MarkerOptions()
                    .position(pin2)
                    .title("End Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
        } else this.googleMap.setMyLocationEnabled(true);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        if(currentOrPast==1) builder.include(marker2.getPosition());
        LatLngBounds bounds = builder.build();

        int activityHeight = this.getWindow().getDecorView().getHeight();
        int activityWidth = this.getWindow().getDecorView().getWidth();

            if(activityHeight==0)activityHeight=1920;
            if(activityWidth==0)activityWidth=1080;

        this.googleMap.moveCamera(CameraUpdateFactory
                .newLatLngBounds(bounds,activityWidth,activityHeight,100));
    }

}
