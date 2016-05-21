package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class menu {
    private static final String AppName = "Dengue";
    private int [] Name = new int[] {
            R.string.menu_hot,
            R.string.menu_hospital,
            R.string.menu_breedingSources,
            R.string.menu_bite,
            R.string.menu_setting,
    };
    private int [] Img = new int[] {
            R.drawable.notification_on,
            R.drawable.hospital_off,
            R.drawable.source_checkin_off,
            R.drawable.mosquito_checkin_off,
            R.drawable.setting_off
    };

    menu(final Activity Main,int index) {
        session Session = new session(Main.getSharedPreferences(AppName, 0));
        if( Session.getData("identity").equals("里長") ) {
            Name = new int[] {
                    R.string.menu_hot,
                    R.string.menu_hospital,
                    R.string.menu_breedingSources,
                    R.string.menu_bite,
                    R.string.menu_reportList,
            };
        }
        switch(index)
        {
            case 0:
                Img[0] = R.drawable.notification_on;
                Img[1] = R.drawable.hospital_off;
                Img[2] = R.drawable.source_checkin_off;
                Img[3] = R.drawable.mosquito_checkin_off;
                Img[4] = R.drawable.setting_off;
                break;
            case 1:
                Img[0] = R.drawable.notification_off;
                Img[1] = R.drawable.hospital_on;
                Img[2] = R.drawable.source_checkin_off;
                Img[3] = R.drawable.mosquito_checkin_off;
                Img[4] = R.drawable.setting_off;
                break;
            case 2:
                Img[0] = R.drawable.notification_off;
                Img[1] = R.drawable.hospital_off;
                Img[2] = R.drawable.source_checkin_on;
                Img[3] = R.drawable.mosquito_checkin_off;
                Img[4] = R.drawable.setting_off;
                break;
            case 3:
                Img[0] = R.drawable.notification_off;
                Img[1] = R.drawable.hospital_off;
                Img[2] = R.drawable.source_checkin_off;
                Img[3] = R.drawable.mosquito_checkin_on;
                Img[4] = R.drawable.setting_off;
                break;
            case 4:
                Img[0] = R.drawable.notification_off;
                Img[1] = R.drawable.hospital_off;
                Img[2] = R.drawable.source_checkin_off;
                Img[3] = R.drawable.mosquito_checkin_off;
                Img[4] = R.drawable.setting_on;
                break;
        }

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
                        intent.setClass(Main, BreedingSource.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_bite:

                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.DrugBite"))
                            break;
                        intent.setClass(Main, Drugbite.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_reportList:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.Report"))
                            break;
                        intent.setClass(Main, Report.class);
                        Main.startActivity(intent);
                        break;
                    case R.string.menu_setting:
                        if (Main.getComponentName().getClassName().equals("com.example.dengue.dengue_android.UserSetting"))
                            break;
                        intent.setClass(Main, UserSetting.class);
                        Main.startActivity(intent);
                        break;
                }
            }
        });
    }
}
