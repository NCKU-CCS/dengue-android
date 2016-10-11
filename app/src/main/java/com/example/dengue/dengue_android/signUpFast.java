package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class signUpFast {
    private static final String AppName = "Dengue";

    signUpFast(final Activity Main, final Intent intent) {
        final session Session = new session(Main.getSharedPreferences(AppName, 0));
        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection connect = null;

                try {
                    URL connect_url = new URL("https://api-test.denguefever.tw/users/fast/");
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
                    }

                    Main.startActivity(intent);
                }
                catch (Exception e) {
                    Main.startActivity(intent);
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

    signUpFast(final Activity Main) {
        final session Session = new session(Main.getSharedPreferences(AppName, 0));
        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection connect = null;

                try {
                    URL connect_url = new URL("https://api-test.denguefever.tw/users/fast/");
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
