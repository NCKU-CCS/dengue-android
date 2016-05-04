package com.example.dengue.dengue_android;
import android.content.SharedPreferences;

public class session {
    private SharedPreferences Session;

    session() {
    }

    public void setSession(SharedPreferences mSession) {
        Session = mSession;
    }

    public void setData(String key, String value) {
        SharedPreferences.Editor editor = Session.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringData(String key) {
        return Session.getString(key, null);
    }

    public void setData(String key, boolean value) {
        SharedPreferences.Editor editor = Session.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanData(String key) {
        return Session.getBoolean(key, false);
    }
}
