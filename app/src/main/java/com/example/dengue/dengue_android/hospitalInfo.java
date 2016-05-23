package com.example.dengue.dengue_android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class hospitalInfo extends FragmentActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hospital_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hospital_info_map);
        mapFragment.getMapAsync(this);
        new menu(this, 1);
        new goBack(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Bundle position = this.getIntent().getExtras();
        String Lat = position.getString("hospital_lat");
        String Lng = position.getString("hospital_lng");
        assert Lat != null;
        assert Lng != null;

        LatLng sydney = new LatLng(Float.parseFloat(Lat), Float.parseFloat(Lng));
        map.addMarker(new MarkerOptions().position(sydney));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
    }
}