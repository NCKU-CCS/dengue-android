package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class userProfile extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);
        new menu(this, 5);

        /*Button logout_btn = (Button) findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                logout();
            }
        });*/
    }

    static public void logout (Activity act){
        final Activity Main = act;
        final session Session = new session(Main.getSharedPreferences(AppName, 0));
        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection connect = null;

                try {
                    URL connect_url = new URL("https://api.denguefever.tw/users/fast/");
                    connect = (HttpsURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        JSONObject output = new JSONObject(sb.toString());
                        Session.setData("user_uuid", output.getString("user_uuid"));
                        Session.setData("token", output.getString("token"));
                        Session.setData("isLogin", "false");

                        Main.finish();
                        Intent intent = new Intent();
                        intent.setClass(Main, UserSetting.class);
                        Main.startActivity(intent);
                    }
                }
                catch (Exception ignored) {
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
