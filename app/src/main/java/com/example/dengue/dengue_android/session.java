package com.example.dengue.dengue_android;

import android.content.SharedPreferences;
import android.util.Log;

public class session {
    private SharedPreferences Session;

    session(SharedPreferences mSession) {
        Session = mSession;
    }

    public void setData(String key, String value) {
        SharedPreferences.Editor editor = Session.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key) {
        return Session.getString(key, null);
    }
}