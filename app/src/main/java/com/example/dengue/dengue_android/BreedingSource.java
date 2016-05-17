package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class BreedingSource extends Activity {
    private static final String AppName = "Dengue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: need to rewrite
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            session Session = new session(getSharedPreferences(AppName, 0));
            Session.setData("breadingSource_img", uri.toString());

            Intent intent = new Intent();
            intent.setClass(this, BreedingSourceSubmit.class);
            startActivity(intent);
        } else if (resultCode == RESULT_CANCELED) {
            onBackPressed();
            Log.i("test", "1");
        } else {
            onBackPressed();
            Log.i("test", "2");
        }
    }
}
