package com.example.dengue.dengue_android;

import android.content.Context;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hsuting on 16/3/8.
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    protected static final String TAG = "MainActivity";
    private TelephonyManager mTelManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient();

        setContentView(R.layout.login);
        login_buttonEvent();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        } else {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    // 'login' button event
    private void login_buttonEvent() {
        if(mTelManager != null) {
            EditText text = (EditText) findViewById(R.id.login_phone_value);
            text.setText(mTelManager.getLine1Number());
        }

        Button login_submitButton = (Button)findViewById(R.id.login_submit);
        login_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                EditText phone = (EditText) findViewById(R.id.login_phone_value);
                Log.i(TAG, phone.getText().toString());
                if( phone.getText().toString().equals("0912345678") ) {
                    setContentView(R.layout.menu_village_chief);
                    menu_buttonsEvent();
                }
                else {
                    setContentView(R.layout.menu);
                    menu_buttonsEvent();
                }
            }
        });
    }

    // 'menu' button event
    private void menu_buttonsEvent() {
        Button menu_breedingSources = (Button)findViewById(R.id.menu_breedingSources);
        menu_breedingSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                setContentView(R.layout.breeding_sources_submit);
                breedingSources_submit_submitButtonEvent();
            }
        });

        Button menu_logout = (Button)findViewById(R.id.menu_logout);
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                setContentView(R.layout.login);
                login_buttonEvent();
            }
        });
    }

    // 'breeding sources submit' submit button event
    private void breedingSources_submit_submitButtonEvent() {
        Spinner breedingSources_submit_spinner = (Spinner)findViewById(R.id.breedingSources_submit_type_value);
        String[] types = {"type1", "type2"};
        ArrayAdapter<String> typelist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        breedingSources_submit_spinner.setAdapter(typelist);

        Button breedingSources_submitButton = (Button) findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                TextView output = (TextView) findViewById(R.id.breedingSources_submit_output);
                EditText description = (EditText) findViewById(R.id.breedingSources_submit_description_value);
                Spinner type = (Spinner) findViewById(R.id.breedingSources_submit_type_value);
                final String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));

                String data = "";
                data += "id: " + mTelManager.getLine1Number() + "\n";
                data += "type:" + type.getSelectedItem().toString() + "\n";
                data += "description: " + description.getText().toString() + "\n";
                data += "date: " + now + "\n";
                data += "lat:" + lat + "\n";
                data += "lon:" + lon + "\n";
                data += "done: false";
                output.setText(data);

            }
        });

        ImageButton breedingSources_goBackButton = (ImageButton) findViewById(R.id.breedingSources_submit_goback);
        breedingSources_goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(R.layout.menu);
                menu_buttonsEvent();
            }
        });
    }
}