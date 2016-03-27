package com.example.dengue.dengue_android;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class hospitalEvent {
    private MainActivity Main;
    private CharSequence[] Name;
    private int number;
    private String[] data;

    Runnable hospitalInfo;

    hospitalEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void sethospitalView(Runnable goBack, Runnable mhospitalInfo) {
        // temp list
        Name = new String[] {"醫院1", "醫院2", "醫院3"};
        number = Name.length;
        hospitalInfo = mhospitalInfo;
        Main.setContentView(R.layout.hospital);

        goBack.run();
        hospitalNumber();
        hospitalList();
    }

    public String[] getData() {
        return data;
    }

    private void hospitalNumber() {
        TextView hospital_number = (TextView) Main.findViewById(R.id.hospital_number);
        String output_number = "您附近有 " + number + " 個醫療院所";
        hospital_number.setText(output_number);
    }

    private void hospitalList() {
        ListView hospital_list = (ListView) Main.findViewById(R.id.hospital_list);
        hospital_list.setAdapter(new hospitalAdapter(Main, Name));
        hospital_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                data = new String[] {"name", "address", "openTime", "tel"};
                hospitalInfo.run();
            }
        });
    }
}
