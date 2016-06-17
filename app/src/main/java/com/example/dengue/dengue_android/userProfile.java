package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class userProfile extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);

        session Session = new session(getSharedPreferences(AppName, 0));
        getData(Session);
    }

    private void setScore(final session Session) {
        TextView score = (TextView) findViewById(R.id.user_score);
        score.setText(Session.getData("score"));

        TextView breeding_source_count = (TextView) findViewById(R.id.user_breeding_source_count);
        breeding_source_count.setText(Session.getData("breeding_source_count"));

        TextView bites_count = (TextView) findViewById(R.id.user_bites_count);
        bites_count.setText(Session.getData("bites_count"));
    }

    private void getData(final session Session) {
        final Activity Main = this;

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
                        Session.setData("score", output.getString("score").equals("") ? "0" : output.getString("score"));
                        Session.setData("breeding_source_count", output.getString("breeding_source_count").equals("") ? "0" : output.getString("breeding_source_count"));
                        Session.setData("bites_count", output.getString("bites_count").equals("") ? "0" : output.getString("bites_count"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setScore(Session);
                                Button logout_button = (Button) findViewById(R.id.logout_btn);
                                logout_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View w) {
                                        logout(Session);
                                    }
                                });
                                new menu(Main, Session.getData("identity").equals("里長") ? 5 : 4);
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setScore(Session);
                                Button logout_button = (Button) findViewById(R.id.logout_btn);
                                logout_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View w) {
                                        logout(Session);
                                    }
                                });
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();
                                new menu(Main, Session.getData("identity").equals("里長") ? 5 : 4);
                            }
                        });
                    }
                }
                catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "登入失敗！請確認網路連線", Toast.LENGTH_SHORT).show();
                            new menu(Main, Session.getData("identity").equals("里長") ? 5 : 4);
                        }
                    });
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
        final Activity Main = this;

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
                        Session.setData("isLogin", "false");
                        Session.setData("identity", "");
                        Session.setData("cookie", "");

                        Intent intent = new Intent();
                        intent.setClass(Main, UserSetting.class);
                        startActivity(intent);
                        Main.finish();
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "無法登出！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "無法登出！請確認網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
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
