package com.example.dengue.dengue_android;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.util.List;

public class gps {
    private Geocoder geocoder;

    gps() {
    }

    public void set(Geocoder mgc) {
        geocoder = mgc;
    }

    public String get(double lat, double lon) {
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(lat, lon, 1);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        if(address != null && address.size() != 0) {
            return address.get(0).getAddressLine(0);
        }

        return null;
    }
}
