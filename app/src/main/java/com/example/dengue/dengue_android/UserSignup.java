package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UserSignup extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_signup);
        new menu(this, 5);

        final EditText name = (EditText) findViewById(R.id.signup_name);
        final EditText phone = (EditText) findViewById(R.id.signup_phone);
        final EditText password = (EditText) findViewById(R.id.signup_password);
        final EditText email = (EditText) findViewById(R.id.signup_email);
        final session Session = new session(getSharedPreferences(AppName, 0));

        Button sign_up_submit = (Button) findViewById(R.id.user_signup_submit);
        sign_up_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                String output = "{";
                output += "\"user_uuid\":\"" + Session.getData("user_uuid") + "\",";
                output += "\"name\":\""+ name.getText().toString() + "\",";
                output += "\"phone\":\""+ phone.getText().toString() + "\",";
                output += "\"password\":\""+ password.getText().toString() + "\",";
                output += "\"email\":\""+ email.getText().toString() + "\"";
                output += "}";

                signUp(output, Session);
            }
        });
    }

    private void signUp(final String data, final session Session) {
        final Activity Main = this;

        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection con = null;

                try {
                    URL connect_url = new URL("https://api.denguefever.tw/users/");
                    con = (HttpsURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.addRequestProperty("Content-Type", "application/json");
                    con.addRequestProperty("Authorization", "Token " +Session.getData("token"));
                    con.connect();

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        JSONObject data = new JSONObject(sb.toString());
                        Session.setData("isLogin", "true");
                        Session.setData("user_uuid", "");
                        Session.setData("token", data.getString("token"));

                        Intent intent = new Intent(Main, userProfile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Main.finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "註冊失敗！此電話已註冊", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "註冊失敗！請確認網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
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