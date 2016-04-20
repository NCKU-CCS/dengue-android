package com.example.dengue.dengue_android;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class menu {
    private int [] Name = new int[]{
        R.string.menu_reportList,
                R.string.menu_bite,
                R.string.menu_breedingSources,
                R.string.menu_hot,
                R.string.menu_hospital
    };
    private int [] Img = new int[]{
        R.drawable.img,
                R.drawable.img,
                R.drawable.img,
                R.drawable.img,
                R.drawable.img
    };

    menu(Activity Main) {
        GridView menu_list = (GridView) Main.findViewById(R.id.menu);
        menu_list.setNumColumns(5);
        menu_list.setAdapter(new menuAdapter(Main, Name, Img));
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (Name[position]) {
                    case R.string.menu_reportList:
                        break;
                    case R.string.menu_bite:
                        break;
                    case R.string.menu_breedingSources:
                        break;
                    case R.string.menu_hot:
                        break;
                    case R.string.menu_hospital:
                        break;
                }
            }
        });
    }
}
