package com.example.dengue.dengue_android;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class menuEvent {
    private MainActivity Main;
    private boolean isVillageChief;
    private session Session;
    private int [] Name;
    private int [] Img;

    private Runnable Logout;
    private Runnable ReportList;
    private Runnable BittenByMosquito;
    private Runnable BreedingSourcesPhoto;
    private Runnable Hot;
    private Runnable Hospital;

    menuEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setMenuView(session mSession,
                            Runnable mLogout,
                            Runnable mReportList,
                            Runnable mBittenByMosquito,
                            Runnable mBreedingSourcePhoto,
                            Runnable mHot,
                            Runnable mHospital
    ) {
        //TODO: need to get information from server
        isVillageChief = false;
        Session = mSession;
        Logout = mLogout;
        ReportList = mReportList;
        BittenByMosquito = mBittenByMosquito;
        BreedingSourcesPhoto = mBreedingSourcePhoto;
        Hot = mHot;
        Hospital = mHospital;
        Main.setContentView(R.layout.menu);

        drawer();
        setList();
        menuList();
    }

    private void drawer() {
        ImageButton menu_button = (ImageButton) Main.findViewById(R.id.menu_menuButton);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                DrawerLayout menu = (DrawerLayout) Main.findViewById(R.id.menu);
                menu.openDrawer(GravityCompat.START);
            }
        });

        // TODO: need to get information from server
        TextView username = (TextView) Main.findViewById(R.id.menu_username);
        username.setText("User name");

        TextView logout = (TextView) Main.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new request(Main, "http://140.116.247.113:11401/users/signout",
                        new Runnable() {
                            @Override
                            public void run() {
                                Session.setData("isLogin", false);
                                Logout.run();
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

    private void setList() {
        if(isVillageChief) {
            Name = new int[]{
                    R.string.menu_reportList,
                    R.string.menu_bite,
                    R.string.menu_breedingSources,
                    R.string.menu_hot,
                    R.string.menu_hospital
            };
            Img = new int[]{
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img
            };

        }
        else {
            Name = new int[]{
                    R.string.menu_bite,
                    R.string.menu_breedingSources,
                    R.string.menu_hot,
                    R.string.menu_hospital
            };
            Img = new int[]{
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img,
                    R.drawable.img
            };
        }
    }

    private void menuList() {
        GridView menu_list = (GridView) Main.findViewById(R.id.menu_list);
        menu_list.setNumColumns(2);
        menu_list.setAdapter(new menuAdapter(Main, Name, Img));
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (Name[position]) {
                    case R.string.menu_reportList:
                        ReportList.run();
                        break;
                    case R.string.menu_bite:
                        BittenByMosquito.run();
                        break;
                    case R.string.menu_breedingSources:
                        BreedingSourcesPhoto.run();
                        break;
                    case R.string.menu_hot:
                        Hot.run();
                        break;
                    case R.string.menu_hospital:
                        Hospital.run();
                        break;
                }
            }
        });
    }
}
