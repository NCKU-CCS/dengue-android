package com.example.dengue.dengue_android;

import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class breedingSourcesSubmitEvent {
    private MainActivity Main;
    private String[] types;
    private TelephonyManager TelManager;
    private double Lat;
    private double Lon;
    private gps gps;
    private Bitmap Img;

    breedingSourcesSubmitEvent(MainActivity mMain) {
        Main = mMain;
        types = new String[] {"住家積水容器", "雜物堆積(髒亂)", "空地積水容器"};
    }

    public void setBreedingSourcesSubmitView(TelephonyManager mTelManager,
                                             double mLat,
                                             double mLon,
                                             gps mGps,
                                             Bitmap mImg,
                                             Runnable goBack
    ) {
        TelManager = mTelManager;
        Lat = mLat;
        Lon = mLon;
        gps = mGps;
        Img = mImg;
        Main.setContentView(R.layout.breeding_sources_submit);

        goBack.run();
        breedingSourcesSubmitImg();
        breedingSourcesSubmitTypeList();
        breedingSourcesSubmitSubmit();
    }

    private void breedingSourcesSubmitImg() {
        ImageView breedingSources_submit_img = (ImageView) Main.findViewById(R.id.breedingSources_submit_img);
        breedingSources_submit_img.setImageBitmap(Img);
    }

    private void breedingSourcesSubmitTypeList() {
        Spinner breedingSources_submit_spinner = (Spinner) Main.findViewById(R.id.breedingSources_submit_type_value);
        breedingSources_submit_spinner.setAdapter(new ArrayAdapter<>(Main, android.R.layout.simple_spinner_item, types));
    }

    private void breedingSourcesSubmitSubmit() {
        Button breedingSources_submitButton = (Button) Main.findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                TextView output = (TextView) Main.findViewById(R.id.breedingSources_submit_output);

                EditText description = (EditText) Main.findViewById(R.id.breedingSources_submit_description_value);
                Spinner type = (Spinner) Main.findViewById(R.id.breedingSources_submit_type_value);
                String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.TRADITIONAL_CHINESE)
                        .format( new Date(System.currentTimeMillis()) );

                String data = "";
                data += "id: " + TelManager.getLine1Number() + "\n";
                data += "type:" + type.getSelectedItem().toString() + "\n";
                data += "description: " + description.getText().toString() + "\n";
                data += "date: " + now + "\n";
                data += "lat:" + Lat + "\n";
                data += "lon:" + Lon + "\n";
                data += "address:" + gps.get(Lat, Lon) + "\n";
                data += "done: false";
                output.setText(data);
            }
        });
    }
}
