package com.example.dengue.dengue_android;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Guild extends AppCompatActivity{
    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    InkPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guild);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        Button b =(Button)findViewById(R.id.button_start);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Intent intent = new Intent();
                intent.setClass(Guild.this, hot.class);
                startActivity(intent);
            }
        });
    }
}
