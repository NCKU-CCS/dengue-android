package com.example.dengue.dengue_android;

import java.net.HttpURLConnection;
import java.net.URL;

public class request {
    request(String url, String method) {
        HttpURLConnection connect = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            URL connect_url = new URL(url);
            connect = (HttpURLConnection) connect_url.openConnection();
            connect.setReadTimeout(10000);
            connect.setConnectTimeout(15000);
            connect.setRequestMethod(method);
            connect.connect();
            if (Thread.interrupted()) {
                throw new InterruptedException();
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
}
