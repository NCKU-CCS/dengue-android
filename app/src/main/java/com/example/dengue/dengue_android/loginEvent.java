package com.example.dengue.dengue_android;

import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class loginEvent {
    private MainActivity Main;
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
            Menu.run();
        }
        else {
            Main.setContentView(R.layout.login);

            EditText login_phoneValue = (EditText) Main.findViewById(R.id.login_phone_value);
            login_phoneValue.setText(Session.getStringData("phone"));

            normalLogin();
            quicklyLogin();
        }
    }

    private void normalLogin() {
        Button login_normalButton = (Button) Main.findViewById(R.id.login_normal);
        login_normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                EditText phoneText = (EditText) Main.findViewById(R.id.login_phone_value);
                final String phone = phoneText.getText().toString();

                EditText passwordText = (EditText) Main.findViewById(R.id.login_password_value);
                final String password = passwordText.getText().toString();

                new request(Main, "http://140.116.247.113:11401/users/signin",
                        "username=" + phone + "&password=" + password,
                        new Runnable() {
                            @Override
                            public void run() {
                                Session.setData("phone", phone);
                                Session.setData("isLogin", true);
                                Menu.run();
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {

                            }
                        }
                );
            }
        });
    }

    private void quicklyLogin() {
        Button login_quicklyButton = (Button) Main.findViewById(R.id.login_quickly);
        login_quicklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Random random = new Random();
                final String phone = TelManager != null ? TelManager.getLine1Number() : String.valueOf(random.nextInt(10000));
                final String password = String.valueOf(random.nextInt(10000));

                Session.setData("phone", phone);
                Session.setData("password", password);

                new request(Main, "http://140.116.247.113:11401/users/signup",
                        "username=" + phone + "&password=" + password + "&is_random=True",
                        new Runnable() {
                            @Override
                            public void run() {
                                new request(Main, "http://140.116.247.113:11401/users/signin",
                                        "username=" + phone + "&password=" + password,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Session.setData("phone", phone);
                                                Session.setData("isLogin", true);
                                                Menu.run();
                                            }
                                        }, new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                }
                                );
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        }
                );
                Session.setData("isLogin", true);
                Menu.run();
            }
        });
    }
}
