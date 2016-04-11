package com.example.dengue.dengue_android;

import android.webkit.WebView;

public class hotEvent {
    private MainActivity Main;

    hotEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setHotView(Runnable goBack) {
        Main.setContentView(R.layout.hot);

        goBack.run();
        hotWeb();
    }

    private void hotWeb() {
        //TODO: need a web
        WebView web = (WebView) Main.findViewById(R.id.hot_web);
        web.getSettings().setJavaScriptEnabled(true);
        web.requestFocus();
        web.loadUrl("http://real.taiwanstat.com/dengue-spatial-temporal/");
    }
}
