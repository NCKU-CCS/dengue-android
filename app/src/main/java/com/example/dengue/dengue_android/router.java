package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class router extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session Session = new session(getSharedPreferences(AppName, 0));
        if( Session.getData("isFirst") != null && Session.getData("isFirst").equals("false") ) {
            Intent intent = new Intent();
            intent.setClass(this, hot.class);
            startActivity(intent);
        }
        else {
            sessionInit(Session);
            Intent intent = new Intent();
            intent.setClass(this, Guild.class);
            new signUpFast(this, intent);
        }

        this.finish();
    }

    private void sessionInit(final session Session) {
        Session.setData("isFirst", "false");
        Session.setData("identity", "一般使用者");
        Session.setData("isLogin", "false");
        Session.setData("cookie", "");
        Session.setData("score", "0");
        Session.setData("breeding_source_count", "0");
        Session.setData("bites_count", "0");
        Session.setData("hospital", "");
    }
}