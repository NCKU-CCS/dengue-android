package com.example.dengue.dengue_android;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class bittenByMosquitoEvent {
    private MainActivity Main;
    private session Session;
    private double Lat;
    private double Lon;
    private gps gps;

    bittenByMosquitoEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setBittenByMosquitoView(session mSession,
                                             double mLat,
                                             double mLon,
                                             gps mGps,
                                             Runnable goBack
    ) {
        Session = mSession;
        Lat = mLat;
        Lon = mLon;
        gps = mGps;
        Main.setContentView(R.layout.bitten_by_mosquito);

        goBack.run();
        bittenByMosquitoUpload();
    }


    private void  bittenByMosquitoUpload() {
        Button bittenByMosquito_Button = (Button) Main.findViewById(R.id.bitten_button);
        bittenByMosquito_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                TextView output = (TextView) Main.findViewById(R.id.bittenByMosquito_output);
                String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.TRADITIONAL_CHINESE)
                        .format( new Date(System.currentTimeMillis()) );

                //TODO: need to submit data to server
                String data = "";
                data += "id: " + Session.getStringData("phone") + "\n";
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
