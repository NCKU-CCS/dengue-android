package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import static android.R.attr.password;

public class UserLogin extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_login);
        new menu(this,4);

        final EditText phone = (EditText) findViewById(R.id.login_phone);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button login_submit = (Button) findViewById(R.id.user_login_submit);
        login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                String output = "{";
                output += "\"phone\":\"" + phone.getText().toString() + "\",";
                output += "\"password\":\"" + password.getText().toString() + "\"";
                output += "}";

                Login(output);
            }
        });

        // TODO: refactoring the same code, onKey & onClick
        password.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() ==       KeyEvent.KEYCODE_ENTER)
                {
                    String output = "{";
                    output += "\"phone\":\"" + phone.getText().toString() + "\",";
                    output += "\"password\":\"" + password.getText().toString() + "\"";
                    output += "}";

                    Login(output);

                    return false;
                }

                return false;
            }
        });
    }


    private void Login(final String data) {
        final session Session = new session(getSharedPreferences(AppName, 0));
        final Activity Main = this;

        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection con = null;

                try {
                    URL connect_url = new URL("https://api.denguefever.tw/users/signin/");
                    con = (HttpsURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.connect();

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
                                Toast.makeText(Main, "登入失敗", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "登入失敗！請確認網路連線", Toast.LENGTH_SHORT).show();
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
