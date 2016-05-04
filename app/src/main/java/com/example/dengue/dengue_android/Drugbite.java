package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class DrugBite extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    private TelephonyManager TelManager;
    //private session Session = new session();
    private gps Gps = new gps();
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugbite);

        TelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient();
        //Session.setSession(getSharedPreferences(AppName, 0));
        Gps.set(new Geocoder(this, Locale.TRADITIONAL_CHINESE));

        drugBiteClick();
        new menu(this);
    }

    private void drugBiteClick() {
        Button bittenByMosquito_Button = (Button)findViewById(R.id.drugbite_button);
        bittenByMosquito_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                //TextView output = (TextView)findViewById(R.id.drugbite_output);
                String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.TRADITIONAL_CHINESE)
                        .format( new Date(System.currentTimeMillis()) );

                //TODO: need to submit data to server
                String data = "database=tainan&lng=120&lon=23";
                //data += "id=" + Session.getStringData("phone") + "&";
                //data += "date= " + now + "&";
                //data += "lng=" + Lon + "&";
                //data += "lat=" + Lat;
                //data += "address=" + gps.get(Lat, Lon) + "&";
                //data += "done=false";
                //output.setText(data);
                sendPost(data);

            }
        });
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Lat = mLastLocation.getLatitude();
            Lon = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(AppName, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(AppName, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    // HTTP POST request
    public void sendPost(final String data){
        Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/bite/insert/";
                HttpURLConnection con = null;
                Log.i(AppName, url);
                try {
                    URL connect_url = new URL(url);
                    con = (HttpURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/form-data");
                    con.connect();
                    Log.i(AppName, data);

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    Log.i(AppName, String.valueOf(responseCode));

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i(AppName, "post success");
                    } else {
                        Log.i(AppName, "post fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        };
        thread.start();
    }

}
