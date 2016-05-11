package com.example.dengue.dengue_android;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

public class goBack {
    goBack(final Activity Main) {
        ImageView goBackButton = (ImageView) Main.findViewById(R.id.go_back);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Main.onBackPressed();
            }
        });
    }
}
