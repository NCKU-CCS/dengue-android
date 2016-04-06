package com.example.dengue.dengue_android;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class loginEvent {
    private MainActivity Main;
    private boolean isVillageChief = false;
    private TelephonyManager TelManager;
    private session Session;

    private Runnable Menu;

    loginEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setLoginView (TelephonyManager mTelManager, session mSession, Runnable mMenu) {
        Menu = mMenu;
        TelManager = mTelManager;
        Session = mSession;

        if( Session.getBooleanData("isLogin") ) {
            String phone = Session.getStringData("phone");
            if(phone != null) {
                isVillageChief = phone.equals("0912345678");
            }
            else {
                isVillageChief = false;
            }
            Menu.run();
        }
        else {
            Main.setContentView(R.layout.login);

            EditText login_phoneValue = (EditText) Main.findViewById(R.id.login_phone_value);
            login_phoneValue.setText(Session.getStringData("phone"));
            EditText login_passwordValue = (EditText) Main.findViewById(R.id.login_password_value);
            login_passwordValue.setText(Session.getStringData("password"));

            normalLogin();
            quicklyLogin();
        }
    }

    public boolean getIsVillageChief () {
        return isVillageChief;
    }

    private void normalLogin() {
        Button login_normalButton = (Button) Main.findViewById(R.id.login_normal);
        login_normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                EditText phone = (EditText) Main.findViewById(R.id.login_phone_value);
                Session.setData("phone", phone.getText().toString());

                EditText password = (EditText) Main.findViewById(R.id.login_password_value);
                Session.setData("password", password.getText().toString());

                isVillageChief = phone.getText().toString().equals("0912345678");
                Session.setData("isLogin", true);
                Menu.run();
            }
        });
    }

    private void quicklyLogin() {
        Button login_quicklyButton = (Button) Main.findViewById(R.id.login_quickly);
        login_quicklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Random random = new Random();
                String phone = TelManager != null ? TelManager.getLine1Number() : String.valueOf(random.nextInt(10000));

                Session.setData("phone", phone);
                Session.setData("password", String.valueOf(random.nextInt(10000)));

                isVillageChief = false;
                Session.setData("isLogin", true);
                Menu.run();
            }
        });
    }
}
