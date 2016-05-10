package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class welcome extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        Intent intent = new Intent();
        intent.setClass(welcome.this, Guild.class);
        startActivity(intent);

        /*
        SharedPreferences settings = getSharedPreferences("dengue", 0);
        boolean test = settings.contains("isFirstIn");
        Log.i("dengue", String.valueOf(test));
        boolean isFirstIn = settings.getBoolean("isFirstIn", true);
        Log.i("dengue", String.valueOf(isFirstIn));


        if(isFirstIn)
        {
            Log.i("dengue", "is FirstIn");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();

            intent.setClass(welcome.this, Guild.class);
        }
        else
        {
            Log.i("dengue", "isn't FirstIn");
            intent.setClass(welcome.this, hot.class);
        }*/

    }
}
