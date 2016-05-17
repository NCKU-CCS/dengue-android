package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserSetting extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final session Session = new session(getSharedPreferences(AppName, 0));
        if(Session.getData("isLogin").equals("true")) {
            setContentView(R.layout.user_profile);

            getProfile(Session);
            Button logout_button = (Button)findViewById(R.id.logout_btn);
            logout_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View w) {
                    logout(Session);

                }
            });
        }
        else if(Session.getData("isLogin").equals("false")) {
            setContentView(R.layout.user_setting);
            settingBtn(this);
        }

        new menu(this);
    }

    private void settingBtn(final Activity Main) {
        Button signup_button = (Button)findViewById(R.id.user_signup_btn);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(Main, UserSignup.class);
                startActivity(intent);
            }
        });

        Button login_button = (Button)findViewById(R.id.user_login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(Main, UserLogin.class);
                startActivity(intent);
            }
        });
    }

    private void getProfile(final session Session) {
        getInfo(Session);
        TextView score = (TextView) findViewById(R.id.user_score);
        score.setText(Session.getData("score"));

        TextView breeding_source_count = (TextView) findViewById(R.id.user_breeding_source_count);
        breeding_source_count.setText(Session.getData("breeding_source_count"));

        TextView bites_count = (TextView) findViewById(R.id.user_bites_count);
        bites_count.setText(Session.getData("bites_count"));
    }

    private void getInfo(final session Session) {
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/users/info/");
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connect.setRequestProperty("Cookie", Session.getData("cookie"));
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    Log.i("test", String.valueOf(responseCode));
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i("Dengue", "get profile success");
                        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        JSONObject output = new JSONObject(sb.toString());
                        Session.setData("score", output.getString("score").equals("") ? "0" : output.getString("score"));
                        Session.setData("breeding_source_count", output.getString("breeding_source_count"));
                        Session.setData("bites_count", output.getString("bites_count"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView score = (TextView) findViewById(R.id.user_score);
                                score.setText(Session.getData("score"));

                                TextView breeding_source_count = (TextView) findViewById(R.id.user_breeding_source_count);
                                breeding_source_count.setText(Session.getData("breeding_source_count"));

                                TextView bites_count = (TextView) findViewById(R.id.user_bites_count);
                                bites_count.setText(Session.getData("bites_count"));
                            }
                        });
                    }
                    else {
                        //TODO: can not connect
                        Log.i("Dengue", "get profile fail");
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

    private void logout (final session Session){
        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/users/signout/");
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connect.setRequestProperty("Cookie", Session.getData("cookie"));
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i("Dengue", "logout success");
                        Session.setData("isLogin", "false");
                        Session.setData("isFirst", "true");

                        Intent intent = new Intent();
                        intent.setClass(UserSetting.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        //TODO: can not connect
                        Log.i("Dengue", "logout fail");
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
