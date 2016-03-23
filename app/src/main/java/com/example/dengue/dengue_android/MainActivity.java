package com.example.dengue.dengue_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    protected static final String TAG = "MainActivity";
    private TelephonyManager mTelManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String lat;
    private String lon;
    private boolean isVillageChief;

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
                if (phone.getText().toString().equals("0912345678")) {
                    isVillageChief = true;
                    setContentView(R.layout.menu_village_chief);
                    menu_buttonsEvent();
                } else {
                    isVillageChief = false;
                    setContentView(R.layout.menu);
                    menu_buttonsEvent();
                }
            }
        });
    }

    // 'menu' button event
    private void menu_buttonsEvent() {
        if(isVillageChief) {
            Button menu_reportList = (Button) findViewById(R.id.menu_reportList);
            menu_reportList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View w) {
                    setContentView(R.layout.report_list);
                    reportList_buttonEvent();
                }
            });
        }

        Button menu_breedingSources = (Button)findViewById(R.id.menu_breedingSources);
        menu_breedingSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(R.layout.breeding_sources_submit);
                breedingSources_submit_submitButtonEvent();
            }
        });

        Button menu_logout = (Button)findViewById(R.id.menu_logout);
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(R.layout.login);
                login_buttonEvent();
            }
        });
    }

    // 'report list' button event
    private void reportList_buttonEvent() {
        goBack(R.layout.menu_village_chief, new Runnable() {
            @Override
            public void run() {
                menu_buttonsEvent();
            }
        });
        final String number = "10";
        final String[] name = new String[] {"地點1", "地點2", "地點3"};
        final String[] isDone = new String[] {"完成", "未查", "完成"};
        CharSequence[] Name = name;
        CharSequence[] IsDone = isDone;

        TextView reportList_number = (TextView) findViewById(R.id.reportList_number);
        reportList_number.setText("還有 " + number + " 個點待查");

        ListView reportList_list = (ListView) findViewById(R.id.reportList_list);
        reportList_list.setAdapter(new reportAdapter(this, Name, IsDone));
        reportList_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setContentView(R.layout.report_list_check);
                String date = "date";
                String type = "type";
                String description = "description";
                reportList_checkButtonEvent(date, type, description);
            }
        });
    }

    // 'report list' check button event
    private void reportList_checkButtonEvent(String date, String type, String description) {
        goBack(R.layout.report_list, new Runnable() {
            @Override
            public void run() {
                reportList_buttonEvent();
            }
        });

        TextView reportList_check_date = (TextView) findViewById(R.id.reportList_check_date);
        TextView reportList_check_type = (TextView) findViewById(R.id.reportList_check_type);
        TextView reportList_check_description = (TextView) findViewById(R.id.reportList_check_description);
        reportList_check_date.setText(reportList_check_date.getText().toString() + date);
        reportList_check_type.setText(reportList_check_type.getText().toString() + type);
        reportList_check_description.setText(reportList_check_description.getText().toString() + description);

        final String[] ways = new String[] {"清除", "告知清理"};
        Button reportList_check_yesButton = (Button) findViewById(R.id.reportList_check_yesButton);
        reportList_check_yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.reportList_check_dialogTitle)
                        .setItems(ways, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setContentView(R.layout.report_list);
                                reportList_buttonEvent();
                            }
                        })
                        .show();
            }
        });

        Button reportList_check_noButton = (Button) findViewById(R.id.reportList_check_noButton);
        reportList_check_noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.report_list);
                reportList_buttonEvent();
            }
        });
    }

    // 'breeding sources submit' submit button event
    private void breedingSources_submit_submitButtonEvent() {
        if(isVillageChief) {
            goBack(R.layout.menu_village_chief, new Runnable() {
                @Override
                public void run() {
                    menu_buttonsEvent();
                }
            });
        }
        else {
            goBack(R.layout.menu, new Runnable() {
                @Override
                public void run() {
                    menu_buttonsEvent();
                }
            });
        }

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
    }

    private void goBack(final int layoutID, final Runnable buttonEvent) {
        ImageButton goBackButton = (ImageButton) findViewById(R.id.goback);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(layoutID);
                buttonEvent.run();
            }
        });
    }
}