package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserSetting extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final session Session = new session(getSharedPreferences(AppName, 0));
        if(Session.getData("isLogin").equals("true")) {
            Intent intent = new Intent();
            intent.setClass(this, userProfile.class);
            startActivity(intent);
            this.finish();
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
                intent.setClass(Main, UserSignup.class);
                startActivity(intent);
                Main.finish();
            }
        });

        Button login_button = (Button) findViewById(R.id.user_login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(Main, UserLogin.class);
                startActivity(intent);
                Main.finish();
            }
        });
    }
}
