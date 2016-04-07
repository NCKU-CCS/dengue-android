package com.example.dengue.dengue_android;

import android.view.View;
import android.widget.ImageButton;

public class goBack {
    private static Runnable run;
    private MainActivity Main;
    goBack(MainActivity mMain) {
        Main = mMain;
    }

    public Runnable run(final Runnable viewEvent) {
         run =  viewEvent;
        return new Runnable() {
            @Override
            public void run() {
                ImageButton goBackButton = (ImageButton) Main.findViewById(R.id.goback);
                goBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View w) {
                        viewEvent.run();
                    }
                });
            }
        };
    }
    public static Runnable getRunnable()
    {
        return run;
    }
}
