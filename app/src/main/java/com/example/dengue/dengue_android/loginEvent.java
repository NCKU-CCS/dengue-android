package com.example.dengue.dengue_android;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginEvent {
    private MainActivity Main;
    private boolean isVillageChief = false;

    private Runnable Menu;

    loginEvent(final MainActivity mMain) {
        Main = mMain;
    }

    public void setLoginView (TelephonyManager mTelManager, Runnable mMenu) {
        Menu = mMenu;
        Main.setContentView(R.layout.login);

        if(mTelManager != null) {
            EditText text = (EditText) Main.findViewById(R.id.login_phone_value);
            text.setText(mTelManager.getLine1Number());
        }

        normalLogin();
        quicklyLogin();
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
                isVillageChief = phone.getText().toString().equals("0912345678");
                Menu.run();
            }
        });
    }

    private void quicklyLogin() {
        Button login_quicklyButton = (Button) Main.findViewById(R.id.login_quickly);
        login_quicklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                isVillageChief = false;
                Menu.run();
            }
        });
    }
}
