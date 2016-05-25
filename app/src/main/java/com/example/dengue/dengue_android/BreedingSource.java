package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

public class breedingSource extends Activity {
    Uri photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(photo == null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                photo = data.getData();

                Bundle bundle = new Bundle();
                bundle.putString("img", data.getData().toString());

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(this, breedingSourceSubmit.class);
                startActivity(intent);
            }
        }
    }
}
