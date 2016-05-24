package com.example.dengue.dengue_android;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

public class gps {
    private static Geocoder geocoder;

    gps(Activity Main) {
        geocoder = new Geocoder(Main, Locale.TRADITIONAL_CHINESE);
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