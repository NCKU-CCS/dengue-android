package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class hospital extends Activity {
    private CharSequence[] Name;
    private CharSequence[] Address;
    private CharSequence[] Phone;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session Session = new session(this);
        getData(Session);

        Name = new String[] {"成大醫院", "新樓醫院", "台南醫院"};
        Address = new String[] {"台南市北區勝利路138號", "台南市北區勝利路138號", "台南市北區勝利路138號"};
        Phone = new String[] {"06 235 3535", "06 235 3535", "06 235 3535"};
        number = Name.length;

        setContentView(R.layout.hospital);
        hospitalNumber();
        hospitalList();
        new menu(this);
    }

    private void hospitalNumber() {
        TextView hospital_number = (TextView) findViewById(R.id.hospital_number);
        String output_number = "您附近有 " + number + " 個醫療院所";
        hospital_number.setText(output_number);
    }

    private void hospitalList() {
        final Activity main = this;
        ListView hospital_list = (ListView) findViewById(R.id.hospital_list);
        hospital_list.setAdapter(new hospitalAdapter(this, Name, Address, Phone));
        hospital_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(main, hospitalInfo.class);
                main.startActivity(intent);
            }
        });
    }

    private void getData(final session Session) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/hospital/nearby/?database=tainan&lng=120.218206&lat=22.993109");
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

                        /*JSONArray output = new JSONArray(sb.toString());
                        ArrayList<String> phone_object = new ArrayList<>();
                        ArrayList<String> address_object = new ArrayList<>();
                        ArrayList<String> phone_object = new ArrayList<>();
                        ArrayList<String> lng_object = new ArrayList<>();
                        ArrayList<String> lat_object = new ArrayList<>();
                        for(int i = 0; i < output.length(); i++){
                            JSONObject object = output.get(i);
                            phone_object.add(output.get(i));
                            Log.i("test", output.get(i).toString());
                        }*/
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
}