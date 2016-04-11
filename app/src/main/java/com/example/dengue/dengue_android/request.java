package com.example.dengue.dengue_android;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class request {
    // Post
    request(final MainActivity Main, final String url, final String data, final Runnable success, final Runnable fail) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL(url);
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setDoInput(true);
                    connect.setDoOutput(true);
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("POST");
                    connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connect.connect();

                    OutputStream output = connect.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        Main.runOnUiThread(success);
                    }
                    else {
                        Main.runOnUiThread(fail);
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

    // Normal Get
    request(final MainActivity Main, final String url, final Runnable success, final Runnable fail) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL(url);
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        Main.runOnUiThread(success);
                    }
                    else {
                        Main.runOnUiThread(fail);
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
