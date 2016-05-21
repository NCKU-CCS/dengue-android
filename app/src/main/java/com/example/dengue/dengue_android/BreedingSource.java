package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BreedingSource extends Activity {
    private static final String AppName = "Dengue";
    private Uri photoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: need to rewrite
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, filename);
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            /*Uri uri = data.getData();
            session Session = new session(getSharedPreferences(AppName, 0));
            Session.setData("breadingSource_img", uri.toString());*/
            Uri uri = null;
            if (data == null) {
                if (photoUri != null) {
                    uri = photoUri;
                }
            } else {
                uri = data.getData();
            }
            session Session = new session(getSharedPreferences(AppName, 0));
            Session.setData("breadingSource_img", uri.toString());

            Intent intent = new Intent();
            //intent.putExtra("uri", uri);
            intent.setClass(this, BreedingSourceSubmit.class);
            startActivity(intent);
        } else if (resultCode == RESULT_CANCELED) {
            onBackPressed();
        } else {
            onBackPressed();
        }
    }
}
