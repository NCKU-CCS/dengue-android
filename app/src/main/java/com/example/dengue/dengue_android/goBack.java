package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

public class goBack {
    goBack(final Activity Main, final Runnable back) {
        ImageView goBackButton = (ImageView) Main.findViewById(R.id.go_back);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                back.run();
            }
        });
    }
}
