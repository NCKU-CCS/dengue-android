package com.example.dengue.dengue_android;

import android.widget.TextView;

public class hospitalInfoEvent {
    private MainActivity Main;
    private String[] data;

    hospitalInfoEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setHospitalInfoView(String[] mData, Runnable goBack) {
        data = mData;
        Main.setContentView(R.layout.hospital_info);

        goBack.run();
        hospitalInfoName();
        hospitalInfoAddress();
        hospitalInfoOpenTime();
        hospitalInfoTel();
    }

    private void hospitalInfoName() {
        TextView hospital_info_name = (TextView) Main.findViewById(R.id.hospital_info_name);
        String output_name = hospital_info_name.getText().toString() + data[0];
        hospital_info_name.setText(output_name);
    }

    private void hospitalInfoAddress() {
        TextView hospital_info_address = (TextView) Main.findViewById(R.id.hospital_info_address);
        String output_address = hospital_info_address.getText().toString() + data[1];
        hospital_info_address.setText(output_address);
    }

    private void hospitalInfoOpenTime() {
        TextView hospital_info_openTime = (TextView) Main.findViewById(R.id.hospital_info_openTime);
        String output_openTime = hospital_info_openTime.getText().toString() + data[2];
        hospital_info_openTime.setText(output_openTime);
    }

    private void hospitalInfoTel() {
        TextView hospital_info_tel = (TextView) Main.findViewById(R.id.hospital_info_tel);
        String output_tel = hospital_info_tel.getText().toString() + data[3];
        hospital_info_tel.setText(output_tel);
    }
}
