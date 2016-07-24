package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class hospital extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private CharSequence[] Name = new CharSequence[]{};
    private CharSequence[] Address = new CharSequence[]{};
    private CharSequence[] Phone = new CharSequence[]{};
    private CharSequence[] Lng = new CharSequence[]{};
    private CharSequence[] Lat = new CharSequence[]{};

    private int number = 0;
    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Location_lat;
    private double Location_lon;
    private int now_choice = 0;
    private boolean refresh = false;
    private Long update_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hospital);
        new menu(this, 1);
        buildGoogleApiClient();

        Date curDate = new Date(System.currentTimeMillis()) ;
        update_time = curDate.getTime();
    }

    private void hospitalNumber() {
        TextView hospital_number = (TextView) findViewById(R.id.hospital_number);
        String output_number = "您附近有 " + number + " 家醫療院所";
        hospital_number.setText(output_number);
    }

    private void hospitalList(CharSequence[] name, CharSequence[] address,
                              CharSequence[] phone, final CharSequence[] lng, final CharSequence[] lat) {
        final Activity main = this;
        final ListView hospital_list = (ListView) findViewById(R.id.hospital_list);
        hospital_list.setDivider(null);
        hospital_list.setAdapter(new hospitalAdapter(this, name, address, phone));
        hospital_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("hospital_lng", lng[position].toString());
                bundle.putString("hospital_lat", lat[position].toString());

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(main, hospitalInfo.class);
                main.startActivity(intent);
            }
        });

        hospital_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View v = hospital_list.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        Date curDate = new Date(System.currentTimeMillis());
                        Long now = curDate.getTime();
                        Long diffTime = (now - update_time) / (1000 * 60);
                        if (diffTime > 1 && refresh) {
                            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            refresh = false;
                            getData();
                        }
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = hospital_list.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        Date curDate = new Date(System.currentTimeMillis());
                        Long now = curDate.getTime();
                        Long diffTime = (now - update_time) / (1000 * 60);
                        if (diffTime > 1) {
                            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            refresh = false;
                            getData();
                        }
                    }
                }
            }
        });
    }

    private void bindClick() {
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
                    hospitalList(Name, Address, Phone, Lng, Lat);

                    hospital_choice_all.setBackgroundResource(R.drawable.choice_border_clicked);
                    hospital_choice_1.setBackgroundResource(R.drawable.choice_border);
                    hospital_choice_2.setBackgroundResource(R.drawable.choice_border);
                }
            }
        });

        hospital_choice_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 1) {
                    now_choice = 1;
                    filterData("醫院");

                    hospital_choice_all.setBackgroundResource(R.drawable.choice_border);
                    hospital_choice_1.setBackgroundResource(R.drawable.choice_border_clicked);
                    hospital_choice_2.setBackgroundResource(R.drawable.choice_border);
                }
            }
        });

        hospital_choice_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 2) {
                    now_choice = 2;
                    filterData("診所");

                    hospital_choice_all.setBackgroundResource(R.drawable.choice_border);
                    hospital_choice_1.setBackgroundResource(R.drawable.choice_border);
                    hospital_choice_2.setBackgroundResource(R.drawable.choice_border_clicked);
                }
            }
        });
    }

    private void filterData(String token) {
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
        hospitalList(temp_name, temp_address, temp_phone, temp_lng, temp_lat);
    }

    private void parseData(String data) throws JSONException {
        if(data.equals("")) {
            return;
        }

        JSONArray output = new JSONArray(data);
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
    }

    private void getData() {
        final session Session = new session(getSharedPreferences(AppName, 0));
        final Activity Main = this;

        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;

                try {
                    URL connect_url = new URL("http://api.denguefever.tw/hospital/nearby/?database=tainan&lng="+Location_lon+"&lat="+Location_lat);
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

                        parseData(sb.toString());
                        Session.setData("hospital", sb.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hospitalNumber();
                                hospitalList(Name, Address, Phone, Lng, Lat);
                                bindClick();

                                Date curDate = new Date(System.currentTimeMillis()) ;
                                update_time = curDate.getTime();
                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                    else {
                        parseData(Session.getData("hospital"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hospitalNumber();
                                hospitalList(Name, Address, Phone, Lng, Lat);
                                bindClick();
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();

                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                }
                catch (Exception e) {
                    try {
                        parseData(Session.getData("hospital"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hospitalNumber();
                                hospitalList(Name, Address, Phone, Lng, Lat);
                                bindClick();
                                Toast.makeText(Main, "確認網路連線以更新資料！", Toast.LENGTH_SHORT).show();

                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    } catch (JSONException ignored) {
                    }
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
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Location_lat = mLastLocation.getLatitude();
            Location_lon = mLastLocation.getLongitude();
            getData();
        }
        else {
            Toast.makeText(this, "請打開定位", Toast.LENGTH_SHORT).show();
            getData();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast.makeText(this, "無法連接google play！", Toast.LENGTH_SHORT).show();
    }
}