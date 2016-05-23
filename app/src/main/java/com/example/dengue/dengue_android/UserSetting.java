package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class userSetting extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final session Session = new session(getSharedPreferences(AppName, 0));
        if(Session.getData("isLogin").equals("true")) {
            if(Session.getData("identity").equals("里長")) {
                Intent intent = new Intent();
                intent.setClass(this, report.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent();
                intent.setClass(this, userProfile.class);
                startActivity(intent);
            }
        }
        else{
            setContentView(R.layout.user_setting);
            new menu(this, 4);
            settingBtn();
        }
    }

    private void settingBtn() {
        final Activity Main = this;

        Button sign_up_button = (Button) findViewById(R.id.user_signup_btn);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(Main, userSignUp.class);
                startActivity(intent);
            }
        });

        Button login_button = (Button) findViewById(R.id.user_login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(Main, userLogin.class);
                startActivity(intent);
            }
        });
    }
}
