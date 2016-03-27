package com.example.dengue.dengue_android;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class reportListEvent {
    private MainActivity Main;
    private CharSequence[] Name;
    private CharSequence[] IsDone;
    private int number;
    private String[] data;

    private Runnable ReportListCheck;

    reportListEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setReportListView(Runnable goBack, Runnable mReportListCheck) {
        // temp list
        Name = new String[]{"地點1", "地點2", "地點3"};
        IsDone = new String[]{"待處理", "待查", "待處理"};
        number = Name.length;
        ReportListCheck = mReportListCheck;
        Main.setContentView(R.layout.report_list);

        goBack.run();
        reportListNumber();
        reportList();
    }

    public String[] getData() {
        return data;
    }

    private void reportListNumber() {
        TextView reportList_number = (TextView) Main.findViewById(R.id.reportList_number);
        String output_number = "還有 " + number + " 個點待查";
        reportList_number.setText(output_number);
    }

    private void reportList() {
        ListView reportList_list = (ListView) Main.findViewById(R.id.reportList_list);
        reportList_list.setAdapter(new reportAdapter(Main, Name, IsDone));
        reportList_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // temp list
                data = new String[] {"date", "type", "description"};
                ReportListCheck.run();
            }
        });
    }
}
