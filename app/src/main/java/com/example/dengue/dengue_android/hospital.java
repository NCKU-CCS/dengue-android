package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class hospital extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private CharSequence[] Name;
    private CharSequence[] Address;
    private CharSequence[] Phone;
    private CharSequence[] Lng;
    private CharSequence[] Lat;
    private int number;
    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Location_lat;
    private double Location_lon;
    private int now_choice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildGoogleApiClient();
    }

    private void hospitalNumber() {
        TextView hospital_number = (TextView) findViewById(R.id.hospital_number);
        String output_number = "您附近有 " + number + " 個醫療院所";
        hospital_number.setText(output_number);
    }

    private void hospitalList(final session Session, CharSequence[] name, CharSequence[] address,
                              CharSequence[] phone, final CharSequence[] lng, final CharSequence[] lat) {
        final Activity main = this;
        ListView hospital_list = (ListView) findViewById(R.id.hospital_list);
        hospital_list.setAdapter(new hospitalAdapter(this, name, address, phone));
        hospital_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session.setData("hospital_lng", lng[position].toString());
                Session.setData("hospital_lat", lat[position].toString());

                Intent intent = new Intent();
                intent.setClass(main, hospitalInfo.class);
                main.startActivity(intent);
            }
        });
    }

    private void bindClick(final session Session) {
        final TextView hospital_choice_all = (TextView) findViewById(R.id.hospital_choice_all);
        final TextView hospital_choice_1 = (TextView) findViewById(R.id.hospital_choice_1);
        final TextView hospital_choice_2 = (TextView) findViewById(R.id.hospital_choice_2);

        hospital_choice_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 0) {
                    now_choice = 0;
                    number = Name.length;
                    hospitalNumber();
                    hospitalList(Session, Name, Address, Phone, Lng, Lat);

                    hospital_choice_all.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                    hospital_choice_1.setBackgroundResource(R.drawable.hospital_choice_border);
                    hospital_choice_2.setBackgroundResource(R.drawable.hospital_choice_border);
                }
            }
        });

        hospital_choice_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 1) {
                    now_choice = 1;
                    filterData(Session, "醫院");

                    hospital_choice_all.setBackgroundResource(R.drawable.hospital_choice_border);
                    hospital_choice_1.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                    hospital_choice_2.setBackgroundResource(R.drawable.hospital_choice_border);
                }
            }
        });

        hospital_choice_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 2) {
                    now_choice = 2;
                    filterData(Session, "診所");

                    hospital_choice_all.setBackgroundResource(R.drawable.hospital_choice_border);
                    hospital_choice_1.setBackgroundResource(R.drawable.hospital_choice_border);
                    hospital_choice_2.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                }
            }
        });
    }

    private void filterData(session Session, String token) {
        ArrayList<String> temp_Name = new ArrayList<>();
        ArrayList<String> temp_Address  = new ArrayList<>();
        ArrayList<String> temp_Phone  = new ArrayList<>();
        ArrayList<String> temp_Lng  = new ArrayList<>();
        ArrayList<String> temp_Lat  = new ArrayList<>();
        for(int i = 0; i < Name.length; i++) {
            if(Name[i].toString().contains(token)) {
                temp_Name.add(Name[i].toString());
                temp_Address.add(Address[i].toString());
                temp_Phone.add(Phone[i].toString());
                temp_Lng.add(Lng[i].toString());
                temp_Lat.add(Lat[i].toString());
            }
        }
        CharSequence[] temp_name = temp_Name.toArray(new String[temp_Name.size()]);
        CharSequence[] temp_address = temp_Address.toArray(new String[temp_Address.size()]);
        CharSequence[] temp_phone = temp_Phone.toArray(new String[temp_Phone.size()]);
        CharSequence[] temp_lng = temp_Lng.toArray(new String[temp_Lng.size()]);
        CharSequence[] temp_lat = temp_Lat.toArray(new String[temp_Lat.size()]);

        number = temp_name.length;
        hospitalNumber();
        hospitalList(Session, temp_name, temp_address, temp_phone, temp_lng, temp_lat);
    }

    private void getData(final session Session, final Activity Main) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/hospital/nearby/?database=tainan&lng="+Location_lon+"&lat="+Location_lat);
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setRequestProperty("Cookie", Session.getData("cookie"));
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        JSONArray output = new JSONArray(sb.toString());
                        ArrayList<String> name_object = new ArrayList<>();
                        ArrayList<String> address_object = new ArrayList<>();
                        ArrayList<String> phone_object = new ArrayList<>();
                        ArrayList<String> lng_object = new ArrayList<>();
                        ArrayList<String> lat_object = new ArrayList<>();
                        for(int i = 0; i < output.length(); i++){
                            JSONObject object = new JSONObject(output.get(i).toString());
                            name_object.add(object.getString("name"));
                            address_object.add(object.getString("address"));
                            phone_object.add(object.getString("phone"));
                            lng_object.add(object.getString("lng"));
                            lat_object.add(object.getString("lat"));
                        }

                        Name = name_object.toArray(new String[name_object.size()]);
                        Address = address_object.toArray(new String[address_object.size()]);
                        Phone = phone_object.toArray(new String[phone_object.size()]);
                        Lng = lng_object.toArray(new String[lng_object.size()]);
                        Lat = lat_object.toArray(new String[lat_object.size()]);
                        number = Name.length;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setContentView(R.layout.hospital);
                                hospitalNumber();
                                hospitalList(Session, Name, Address, Phone, Lng, Lat);
                                bindClick(Session);
                                new menu(Main);
                            }
                        });
                    }
                    else {
                        //TODO: can not connect
                        Log.i("Dengue", "can not get data");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (connect != null) {
                        connect.disconnect();
                    }
                }
            }
        };
        thread.start();
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
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Location_lat = mLastLocation.getLatitude();
            Location_lon = mLastLocation.getLongitude();
            session Session = new session(getSharedPreferences(AppName, 0));
            getData(Session, this);
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
}