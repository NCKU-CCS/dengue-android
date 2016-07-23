package com.example.dengue.dengue_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;

public class hot extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle check = this.getIntent().getExtras();

        if(check != null) {
            Boolean first = check.getBoolean("first");

            if(first) {
                new AlertDialog.Builder(this)
                        .setTitle("test")
                        .setMessage("information")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        }

        setContentView(R.layout.hot);
        setWeb();
        new menu(this, 0);
    }

    private void setWeb() {
        WebView web = (WebView) findViewById(R.id.hot_web);
        web.getSettings().setJavaScriptEnabled(true);
        web.requestFocus();
        //web.loadUrl("https://chihsuan.github.io/real.taiwanstat.com/dengue-vis/");
        web.loadUrl("https://abz53378.github.io/dengue-vis/");
    }
}