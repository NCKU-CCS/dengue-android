package com.example.dengue.dengue_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nana on 2016/4/20.
 */
public class Drugbite extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugbite);

        drugBiteClick();
    }


    private void drugBiteClick() {
        Button bittenByMosquito_Button = (Button)findViewById(R.id.drugbite_button);
        bittenByMosquito_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                /*TextView output = (TextView)findViewById(R.id.drugbite_output);
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
                output.setText(data);*/
            }
        });
    }
}
