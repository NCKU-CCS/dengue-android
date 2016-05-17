package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nana on 2016/5/14.
 */
public class UserLogin extends Activity {
    private static final String AppName = "Dengue";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final session Session = new session(getApplicationContext());
        final Activity Main = this;
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
        new menu(this);
    }

    private void signin(final String data) {
        final session Session = new session(this);
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
}
