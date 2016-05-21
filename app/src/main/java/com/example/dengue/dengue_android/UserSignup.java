package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UserSignup extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity Main = this;
        setContentView(R.layout.user_signup);
        final EditText name = (EditText) findViewById(R.id.signup_name);
        final EditText phone = (EditText) findViewById(R.id.signup_phone);
        final EditText password = (EditText) findViewById(R.id.signup_password);
        final EditText email = (EditText) findViewById(R.id.signup_email);
        Button signup_submit = (Button) findViewById(R.id.user_signup_submit);
        signup_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                String output = "";
                output += "name="+ name.getText().toString();
                output += "&phone="+ phone.getText().toString();
                output += "&password="+ password.getText().toString();
                output += "&email="+ email.getText().toString();

                signup(Main, output);
            }
        });
        new menu(this,4);
    }

    private void signup(final Activity Main, final String data)
    {
        final session Session = new session(getSharedPreferences(AppName, 0));
        Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/users/signup/";
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
                        Log.i(AppName, "sign up success");
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
                        intent.setClass(Main, UserSetting.class);
                        startActivity(intent);
                    } else {
                        Log.i(AppName, "sign up fail");

                        Intent intent = new Intent();
                        intent.setClass(Main, UserSetting.class);
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
}
