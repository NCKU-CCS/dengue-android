package com.example.hsuting.dengue_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by hsuting on 16/3/8.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_buttonEvent();
    }

    // 'login' button event
    private void login_buttonEvent() {
        final TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(mTelManager != null) {
            EditText text = (EditText) findViewById(R.id.login_phone_value);
            text.setText(mTelManager.getLine1Number());
        }

        Button login_submitButton = (Button)findViewById(R.id.login_submit);
        login_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(R.layout.menu);
                menu_buttonsEvent();
            }
        });
    }

    // 'menu' button event
    private void menu_buttonsEvent() {
        Button menu_breedingSources = (Button)findViewById(R.id.menu_breedingSources);
        menu_breedingSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                setContentView(R.layout.breeding_sources_submit);
                breedingSources_submit_submitButtonEvent();
            }
        });

        Button menu_logout = (Button)findViewById(R.id.menu_logout);
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                setContentView(R.layout.login);
                login_buttonEvent();
            }
        });
    }

    // 'breeding sources submit' submit button event
    private void breedingSources_submit_submitButtonEvent() {
        final TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Button breedingSources_submitButton = (Button) findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                TextView output = (TextView) findViewById(R.id.breedingSources_submit_output);
                EditText text = (EditText) findViewById(R.id.breedingSources_submit_editText);
                String data = "";

                data += "phone:" + mTelManager.getLine1Number() + "\n";
                data += "description:" + text.getText().toString();
                output.setText(data);
            }
        });

        ImageButton breedingSources_goBackButton = (ImageButton) findViewById(R.id.breedingSources_submit_goback);
        breedingSources_goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                setContentView(R.layout.menu);
                menu_buttonsEvent();
            }
        });
    }
}