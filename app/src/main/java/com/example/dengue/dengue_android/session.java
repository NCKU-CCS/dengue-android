package com.example.dengue.dengue_android;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class session {
    private Context context;
    session(Context mContext) {
        context = mContext;
    }

    public void setData(String FileName, String data) {
        try {
            FileOutputStream file = context.openFileOutput(FileName, Context.MODE_PRIVATE);
            file.write(data.getBytes());
        } catch (FileNotFoundException e) {
            Log.i("Dengue", "can not save data.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getData(String FileName) {
        try {
            FileInputStream file = context.openFileInput(FileName);
            StringBuffer sb = new StringBuffer();
            int ch;
            while ((ch = file.read()) != -1) {
                sb.append((char) ch);
            }
            return new String(sb);
        } catch (FileNotFoundException e) {
            Log.i("Dengue", "can not save data.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}