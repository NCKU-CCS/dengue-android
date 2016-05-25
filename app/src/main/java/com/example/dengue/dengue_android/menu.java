package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class menu {
    private static final String AppName = "Dengue";

    menu(final Activity Main, int index) {
        session Session = new session(Main.getSharedPreferences(AppName, 0));
        final int[] Name = new int[]{
                R.string.menu_hot,
                R.string.menu_hospital,
                R.string.menu_breedingSources,
                R.string.menu_bite,
                (Session.getData("identity").equals("里長") ? R.string.menu_reportList : R.string.menu_setting),
        };
        final int[] Img = new int[]{
                (index == 0 ? R.drawable.notification_on : R.drawable.notification_off),
                (index == 1 ? R.drawable.hospital_on : R.drawable.hospital_off),
                (index == 2 ? R.drawable.source_checkin_on : R.drawable.source_checkin_off),
                (index == 3 ? R.drawable.mosquito_checkin_on : R.drawable.mosquito_checkin_off),
                (index == 4 ? R.drawable.setting_on : R.drawable.setting_off),
        };

        bindClick(Main, Name, Img);
        checkCookie(Main, Session);
    }

    private void checkCookie(final Activity Main, final session Session) {
        if(Session.getData("cookie").equals("")) {
            new signUpFast(Main);
        }
    }

    private void bindClick(final Activity Main, final int[] Name, final int[] Img) {
        final Intent intent = new Intent();
        GridView menu_list = (GridView) Main.findViewById(R.id.menu);
        menu_list.setNumColumns(5);
        menu_list.setAdapter(new menuAdapter(Main, Name, Img));
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (Name[position]) {
                    case R.string.menu_hot:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.hot"))
                            break;
                        intent.setClass(Main, hot.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_hospital:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.hospital"))
                            break;
                        intent.setClass(Main, hospital.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_breedingSources:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.BreedingSources"))
                            break;
                        intent.setClass(Main, breedingSource.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_bite:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.DrugBite"))
                            break;
                        intent.setClass(Main, drugBite.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_reportList:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.report"))
                            break;
                        intent.setClass(Main, report.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_setting:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.userSetting"))
                            break;
                        intent.setClass(Main, userSetting.class);
                        Main.startActivity(intent);
                        break;
                }
            }
        });
    }
}
