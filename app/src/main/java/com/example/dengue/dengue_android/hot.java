package com.example.dengue.dengue_android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class hot extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hot);
        setWeb();
        new menu(this);
    }

    private void setWeb() {
        WebView web = (WebView) findViewById(R.id.hot_web);
        web.getSettings().setJavaScriptEnabled(true);
        web.requestFocus();
        web.loadUrl("http://real.taiwanstat.com/dengue-spatial-temporal/");
    }
}
