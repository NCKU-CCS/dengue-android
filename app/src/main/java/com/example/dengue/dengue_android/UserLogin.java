package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UserLogin extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_login);
        final EditText phone = (EditText) findViewById(R.id.login_phone);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button login_submit = (Button) findViewById(R.id.user_login_submit);
        login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                String output = "";
                output += "phone="+ phone.getText().toString();
                output += "&password="+ password.getText().toString();

                signin(output);
            }
        });
        session Session = new session(getSharedPreferences(AppName, 0));
        getIdentity(this, Session);

        new menu(this,4);
    }

    private void signin(final String data) {
        final session Session = new session(getSharedPreferences(AppName, 0));
        Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/users/signin/";
                HttpURLConnection con = null;
                Log.i(AppName, url);
                try {
                    URL connect_url = new URL(url);
                    con = (HttpURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("Cookie", Session.getData("cookie"));
                    con.connect();

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i(AppName, "login success");
                        Session.setData("isLogin", "true");

                        final String COOKIES_HEADER = "Set-Cookie";
                        CookieManager msCookieManager = new CookieManager();
                        Map<String, List<String>> headerFields = con.getHeaderFields();
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
                        intent.setClass(UserLogin.this, UserSetting.class);
                        startActivity(intent);
                    } else {
                        Log.i(AppName, "login fail");
                        Session.setData("isLogin", "false");

                        Intent intent = new Intent();
                        intent.setClass(UserLogin.this, UserLogin.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
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

                        JSONObject output = new JSONObject(sb.toString());
                        Session.setData("identity", output.getString("identity"));
                        Session.setData("score", output.getString("score"));
                        Session.setData("breeding_source_count", output.getString("breeding_source_count"));
                        Session.setData("bites_count", output.getString("bites_count"));
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
