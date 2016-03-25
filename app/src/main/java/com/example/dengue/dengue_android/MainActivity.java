package com.example.dengue.dengue_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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

    // 'login' button event
    private void login_buttonEvent() {
        if(mTelManager != null) {
            EditText text = (EditText) findViewById(R.id.login_phone_value);
            text.setText(mTelManager.getLine1Number());
        }

        Button login_normalButton = (Button)findViewById(R.id.login_normal);
        login_normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                EditText phone = (EditText) findViewById(R.id.login_phone_value);
                isVillageChief = phone.getText().toString().equals("0912345678");
                setContentView(R.layout.menu);
                menu_buttonsEvent();
            }
        });

        Button login_quicklyButton = (Button)findViewById(R.id.login_quickly);
        login_quicklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                isVillageChief = false;
                setContentView(R.layout.menu);
                menu_buttonsEvent();
            }
        });
    }

    // 'menu' button event
    private void menu_buttonsEvent() {
        final DrawerLayout menu = (DrawerLayout) findViewById(R.id.menu);
        ImageButton menu_button = (ImageButton) findViewById(R.id.menu_menuButton);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                menu.openDrawer(GravityCompat.START);
            }
        });

        TextView username = (TextView) findViewById(R.id.menu_username);
        username.setText("User name");
        TextView logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.login);
                login_buttonEvent();
            }
        });

        final int [] Name;
        final int [] Img;
        if(isVillageChief) {
            Name = new int[]{
                    R.string.menu_reportList,
                    R.string.menu_bite,
                    R.string.menu_breedingSources,
                    R.string.menu_hot,
                    R.string.menu_hospital
            };
            Img = new int[]{
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img
            };

        }
        else {
            Name = new int[]{
                    R.string.menu_bite,
                    R.string.menu_breedingSources,
                    R.string.menu_hot,
                    R.string.menu_hospital
            };
            Img = new int[]{
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img
            };
        }

        GridView menu_list = (GridView) findViewById(R.id.menu_grid);
        menu_list.setNumColumns(2);
        menu_list.setAdapter(new menuAdapter(this, Name, Img));
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (Name[position]) {
                    case R.string.menu_reportList:
                        setContentView(R.layout.report_list);
                        reportList_buttonEvent();
                        break;
                    case R.string.menu_bite:
                        break;
                    case R.string.menu_breedingSources:
                        setContentView(R.layout.breeding_sources_submit);
                        breedingSources_submit_submitButtonEvent();
                        break;
                    case R.string.menu_hot:
                        setContentView(R.layout.hot);
                        hot_buttonEvent();
                        break;
                    case R.string.menu_hospital:
                        setContentView(R.layout.hospital);
                        hospital_buttonEvent();
                        break;
                }
            }
        });
    }

    // 'report list' button event
    private void reportList_buttonEvent() {
        goBack(R.layout.menu, new Runnable() {
            @Override
            public void run() {
                menu_buttonsEvent();
            }
        });

        CharSequence[] Name = new String[] {"地點1", "地點2", "地點3"};
        CharSequence[] IsDone = new String[] {"待處理", "待查", "待處理"};
        final int number = Name.length;

        TextView reportList_number = (TextView) findViewById(R.id.reportList_number);
        String output_number = "還有 " + number + " 個點待查";
        reportList_number.setText(output_number);

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
        String output_date = reportList_check_date.getText().toString() + date;
        String output_type = reportList_check_type.getText().toString() + type;
        String output_description = reportList_check_description.getText().toString() + description;
        reportList_check_date.setText(output_date);
        reportList_check_type.setText(output_type);
        reportList_check_description.setText(output_description);

        final String[] ways = new String[] {"已處理", "通知處理"};
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
        goBack(R.layout.menu, new Runnable() {
                @Override
                public void run() {
                    menu_buttonsEvent();
                }
            });

        Spinner breedingSources_submit_spinner = (Spinner)findViewById(R.id.breedingSources_submit_type_value);
        String[] types = {"住家積水容器", "雜物堆積(髒亂)", "空地積水容器"};
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

    // 'hot' button Event
    private void hot_buttonEvent() {
        goBack(R.layout.menu, new Runnable() {
            @Override
            public void run() {
                menu_buttonsEvent();
            }
        });

        WebView web = (WebView) findViewById(R.id.hot_web);
        web.getSettings().setJavaScriptEnabled(true);
        web.requestFocus();
        web.loadUrl("http://real.taiwanstat.com/dengue-spatial-temporal/");
    }

    // 'hospital' button Event
    private void hospital_buttonEvent() {
        goBack(R.layout.menu, new Runnable() {
                @Override
                public void run() {
                    menu_buttonsEvent();
                }
            });

        CharSequence[] Name = new String[] {"醫院1", "醫院2", "醫院3"};
        final int number = Name.length;

        TextView hospital_number = (TextView) findViewById(R.id.hospital_number);
        String output_number = "您附近有 " + number + " 個醫療院所";
        hospital_number.setText(output_number);

        ListView hospital_list = (ListView) findViewById(R.id.hospital_list);
        hospital_list.setAdapter(new hospitalAdapter(this, Name));
        hospital_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setContentView(R.layout.hospital_info);
                String name = "name";
                String address = "address";
                String openTime = "openTime";
                String tel = "tel";
                hospital_infoButtonEvent(name, address, openTime, tel);
            }
        });
    }

    // 'hospital' info button event
    private void hospital_infoButtonEvent(String name, String address, String openTime, String tel) {
        goBack(R.layout.hospital, new Runnable() {
            @Override
            public void run() {
                hospital_buttonEvent();
            }
        });

        TextView hospital_info_name = (TextView) findViewById(R.id.hospital_info_name);
        TextView hospital_info_address = (TextView) findViewById(R.id.hospital_info_address);
        TextView hospital_info_openTime = (TextView) findViewById(R.id.hospital_info_openTime);
        TextView hospital_info_tel = (TextView) findViewById(R.id.hospital_info_tel);
        String output_name = hospital_info_name.getText().toString() + name;
        String output_address = hospital_info_address.getText().toString() + address;
        String output_openTime = hospital_info_openTime.getText().toString() + openTime;
        String output_tel = hospital_info_tel.getText().toString() + tel;
        hospital_info_name.setText(output_name);
        hospital_info_address.setText(output_address);
        hospital_info_openTime.setText(output_openTime);
        hospital_info_tel.setText(output_tel);
    }
}