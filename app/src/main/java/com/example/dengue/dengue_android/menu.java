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
        final int[] Name = Session.getData("identity").equals("里長") ?
                new int[]{
                        R.string.menu_hot,
                        R.string.menu_hospital,
                        R.string.menu_breedingSources,
                        R.string.menu_bite,
                        R.string.menu_reportList,
                        R.string.menu_setting
                } : new int[]{
                        R.string.menu_hot,
                        R.string.menu_hospital,
                        R.string.menu_breedingSources,
                        R.string.menu_bite,
                        R.string.menu_setting
                };

        final int[] Img = Session.getData("identity").equals("里長") ? new int[]{
                (index == 0 ? R.drawable.notification_on : R.drawable.notification_off),
                (index == 1 ? R.drawable.hospital_on : R.drawable.hospital_off),
                (index == 2 ? R.drawable.source_checkin_on : R.drawable.source_checkin_off),
                (index == 3 ? R.drawable.mosquito_checkin_on : R.drawable.mosquito_checkin_off),
                (index == 4 ? R.drawable.setting_on : R.drawable.setting_off),
                (index == 5 ? R.drawable.setting_on : R.drawable.setting_off)
        } : new int[] {
                (index == 0 ? R.drawable.notification_on : R.drawable.notification_off),
                (index == 1 ? R.drawable.hospital_on : R.drawable.hospital_off),
                (index == 2 ? R.drawable.source_checkin_on : R.drawable.source_checkin_off),
                (index == 3 ? R.drawable.mosquito_checkin_on : R.drawable.mosquito_checkin_off),
                (index == 4 ? R.drawable.setting_on : R.drawable.setting_off)
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
        final session Session = new session(Main.getSharedPreferences(AppName, 0));

        GridView menu_list = (GridView) Main.findViewById(R.id.menu);
        menu_list.setNumColumns( Session.getData("identity").equals("里長") ? 6 : 5 );
        menu_list.setAdapter(new menuAdapter(Main, Name, Img));
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (Name[position]) {
                    case R.string.menu_hot:
                        if (Main.getComponentName().getClassName().equals(hot.class.getName()))
                            break;
                        intent.setClass(Main, hot.class);
                        Main.startActivity(intent);
                        break;

                    case R.string.menu_hospital:
                        if (Main.getComponentName().getClassName().equals(hospital.class.getName()) ||
                                Main.getComponentName().getClassName().equals(hospitalInfo.class.getName()))
                            break;
                        intent.setClass(Main, hospital.class);
                        Main.startActivity(intent);
                        break;

                    case R.string.menu_breedingSources:
                        if (Main.getComponentName().getClassName().equals(BreedingSource.class.getName()) ||
                                Main.getComponentName().getClassName().equals(BreedingSourceSubmit.class.getName()))
                            break;
                        intent.setClass(Main, BreedingSource.class);
                        Main.startActivity(intent);
                        break;

                    case R.string.menu_bite:
                        if (Main.getComponentName().getClassName().equals(Drugbite.class.getName()))
                            break;
                        intent.setClass(Main, Drugbite.class);
                        Main.startActivity(intent);
                        break;

                    case R.string.menu_reportList:
                        if (Main.getComponentName().getClassName().equals(Report.class.getName()))
                            break;
                        intent.setClass(Main, Report.class);
                        Main.startActivity(intent);
                        break;

                    case R.string.menu_setting:
                        if (Main.getComponentName().getClassName().equals(UserLogin.class.getName()) ||
                                Main.getComponentName().getClassName().equals(UserSetting.class.getName()) ||
                                Main.getComponentName().getClassName().equals(UserSignup.class.getName()) ||
                                Main.getComponentName().getClassName().equals(userProfile.class.getName()))
                            break;
                        intent.setClass(Main, UserSetting.class);
                        Main.startActivity(intent);
                        break;
                }
            }
        });
    }
}
