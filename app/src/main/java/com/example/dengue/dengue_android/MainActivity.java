package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);
        session Session = new session(getSharedPreferences(AppName, 0));
        if( Session.getData("isLogin") != null && Session.getData("isLogin").equals("true") ) {
            getIdentity(this, Session);
            Intent intent = new Intent();
            intent.setClass(this, hot.class);
            startActivity(intent);
        }
        else {
            signUpFast(this, Session);
        }
    }

    private void signUpFast(final Activity Main, final session Session) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/users/signup/fast/");
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
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

                        JSONObject output = new JSONObject(sb.toString());
                        Session.setData("user_uuid", output.getString("user_uuid"));
                        Session.setData("isLogin", "true");
                        Session.setData("identity", "一般使用者");

                        final String COOKIES_HEADER = "Set-Cookie";
                        CookieManager msCookieManager = new CookieManager();
                        Map<String, List<String>> headerFields = connect.getHeaderFields();
                        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                        if(cookiesHeader != null)
                        {
                            for (String cookie : cookiesHeader)
                            {
                                String str1 = HttpCookie.parse(cookie).get(0).toString();
                                String str2 = "csrftoken=";
                                if(str1.toLowerCase().contains(str2.toLowerCase())) {
                                    continue;
                                }
                                msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                                Session.setData("cookie", HttpCookie.parse(cookie).get(0).toString());
                            }
                        }

                        Intent intent = new Intent();
                        intent.setClass(Main, welcome.class);
                        startActivity(intent);
                    }
                    else {
                        //TODO: can not connect
                        Log.i("Dengue", "can not sign up fast");
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

    private void getIdentity(final Activity Main, final session Session) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/users/info/");
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
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

                        JSONObject output = new JSONObject(sb.toString());
                        Session.setData("identity", output.getString("identity"));
                        Intent intent = new Intent();
                        intent.setClass(Main, hot.class);
                        startActivity(intent);
                    }
                    else {
                        //TODO: can not connect
                        Log.i("Dengue", "can not get identity");
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