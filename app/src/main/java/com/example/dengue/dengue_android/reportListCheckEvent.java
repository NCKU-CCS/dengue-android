package com.example.dengue.dengue_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class reportListCheckEvent {
    private MainActivity Main;
    private String[] ways;
    private String[] data;

    private Runnable ReportList;

    reportListCheckEvent(MainActivity mMain) {
        Main = mMain;
        ways = new String[] {"已處理", "通知處理"};
    }

    public void setReportListCheckView(String[] mData, Runnable goBack, Runnable mReportList) {
        data = mData;
        ReportList = mReportList;
        Main.setContentView(R.layout.report_list_check);

        goBack.run();
        reportListCheckDate();
        reportListCheckType();
        reportListCheckDescription();
        reportListCheckYesButton();
        reportListCheckNoButton();
    }

    private void reportListCheckDate() {
        TextView reportList_check_date = (TextView) Main.findViewById(R.id.reportList_check_date);
        String output_date = reportList_check_date.getText().toString() + data[0];
        reportList_check_date.setText(output_date);
    }

    private void reportListCheckType() {
        TextView reportList_check_type = (TextView) Main.findViewById(R.id.reportList_check_type);
        String output_type = reportList_check_type.getText().toString() + data[1];
        reportList_check_type.setText(output_type);
    }

    private void reportListCheckDescription() {
        TextView reportList_check_description = (TextView) Main.findViewById(R.id.reportList_check_description);
        String output_description = reportList_check_description.getText().toString() + data[2];
        reportList_check_description.setText(output_description);
    }

    private void reportListCheckYesButton() {
        Button reportList_check_yesButton = (Button) Main.findViewById(R.id.reportList_check_yesButton);
        reportList_check_yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Main)
                        .setTitle(R.string.reportList_check_dialogTitle)
                        .setItems(ways, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ReportList.run();
                            }
                        })
                        .show();
            }
        });
    }

    private void reportListCheckNoButton() {
        Button reportList_check_noButton = (Button) Main.findViewById(R.id.reportList_check_noButton);
        reportList_check_noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportList.run();
            }
        });
    }
}
