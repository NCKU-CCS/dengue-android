package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class menu {
    private int [] Name = new int[] {
            R.string.menu_hot,
            R.string.menu_hospital,
            R.string.menu_breedingSources,
            R.string.menu_bite,
            R.string.menu_setting,
    };
    private int [] Img = new int[] {
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img,
            R.drawable.img
    };

    menu(final Activity Main) {
        session Session = new session(Main.getApplicationContext());
        if( Session.getData("isLogin").equals("里長") ) {
            Name = new int[] {
                    R.string.menu_hot,
                    R.string.menu_hospital,
                    R.string.menu_breedingSources,
                    R.string.menu_bite,
                    R.string.menu_reportList,
            };
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
                        break;
                    case R.string.menu_bite:
                        break;
                    case R.string.menu_reportList:
                        break;
                }
            }
        });
    }
}
