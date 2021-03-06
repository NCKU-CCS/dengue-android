package com.example.dengue.dengue_android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class Report extends Activity {
    private CharSequence[] Img = new CharSequence[]{};
    private CharSequence[] Type = new CharSequence[]{};
    private CharSequence[] Address = new CharSequence[]{};
    private CharSequence[] Description = new CharSequence[]{};
    private CharSequence[] Date = new CharSequence[]{};
    private CharSequence[] Status = new CharSequence[]{};
    private CharSequence[] Lat = new CharSequence[]{};
    private CharSequence[] Lon = new CharSequence[]{};

    private int number;
    private static final String AppName = "Dengue";
    private int now_choice = 0;
    private boolean refresh = false;
    private Long update_time;
    private String now_type = "待審核";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report);
        new menu(this, 4);
        getData("待審核");
        getNumber("待審核");

        Date curDate = new Date(System.currentTimeMillis()) ;
        update_time = curDate.getTime();

        Button logout_btn = (Button) findViewById(R.id.logout_btn);

        final session Session = new session(getSharedPreferences(AppName, 0));
        if (Session.getData("isLogin").equals("true")) {
            logout_btn.setVisibility(View.VISIBLE);
            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View w) {
                    userProfile.logout(Report.this);
                }
            });
        }
    }

    private void reportListNumber() {
        TextView reportList_number = (TextView) findViewById(R.id.reportList_number);
        String output_number = null;
        switch (now_choice) {
            case 0:
                output_number = "您有 " + number + " 個待審核點";
                break;
            case 1:
                output_number = "您有 " + number + " 個已通過點";
                break;
            case 2:
                output_number = "您有 " + number + " 個未通過點";
                break;
        }
        reportList_number.setText(output_number);
    }

    private void reportList() {
        final Activity Main = this;
        final ListView report_list = (ListView) findViewById(R.id.reportList_list);
        report_list.setDivider(null);
        report_list.setAdapter(new ReportAdapter(this, Img, Type, Address,
                Description, Date, Status, Lat, Lon, Main));

        report_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View v = report_list.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        Date curDate = new Date(System.currentTimeMillis());
                        Long now = curDate.getTime();
                        Long diffTime = (now - update_time) / (1000 * 60);
                        if (diffTime > 1 && refresh) {
                            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            refresh = false;
                            getData(now_type);
                        }
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = report_list.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        Date curDate = new Date(System.currentTimeMillis());
                        Long now = curDate.getTime();
                        Long diffTime = (now - update_time) / (1000 * 60);
                        if (diffTime > 1) {
                            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            refresh = false;
                            getData(now_type);
                        }
                    }
                }
            }
        });
    }

    private void bindClick() {
        final TextView report_wait = (TextView) findViewById(R.id.reportList_type_wait);
        final TextView report_not = (TextView) findViewById(R.id.reportList_type_not);
        final TextView report_done = (TextView) findViewById(R.id.reportList_type_done);

        report_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 0) {
                    now_choice = 0;
                    getData("待審核");
                    getNumber("待審核");
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    report_wait.setBackgroundResource(R.drawable.choice_border_clicked);
                    report_done.setBackgroundResource(R.drawable.choice_border);
                    report_not.setBackgroundResource(R.drawable.choice_border);
                }
            }
        });

        report_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 1) {
                    now_choice = 1;
                    getData("已通過");
                    getNumber("已通過");
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    report_wait.setBackgroundResource(R.drawable.choice_border);
                    report_done.setBackgroundResource(R.drawable.choice_border_clicked);
                    report_not.setBackgroundResource(R.drawable.choice_border);
                }
            }
        });

        report_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 2) {
                    now_choice = 2;
                    getData("未通過");
                    getNumber("未通過");
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    report_wait.setBackgroundResource(R.drawable.choice_border);
                    report_done.setBackgroundResource(R.drawable.choice_border);
                    report_not.setBackgroundResource(R.drawable.choice_border_clicked);
                }
            }
        });
    }

    private void parseData(String data) throws JSONException {
        if(data.equals("")) {
            return;
        }

        JSONArray output = new JSONArray(data);
        ArrayList<String> img_object = new ArrayList<>();
        ArrayList<String> type_object = new ArrayList<>();
        ArrayList<String> address_object = new ArrayList<>();
        ArrayList<String> description_object = new ArrayList<>();
        ArrayList<String> date_object = new ArrayList<>();
        ArrayList<String> status_object = new ArrayList<>();
        ArrayList<String> lat_object = new ArrayList<>();
        ArrayList<String> lon_object = new ArrayList<>();

        for(int i = 0; i < output.length(); i++){
            JSONObject object = new JSONObject(output.get(i).toString());
            img_object.add(object.getString("photo_base64"));
            type_object.add(object.getString("source_type"));
            if(!object.getString("modified_address").equals("")) {
                address_object.add(object.getString("modified_address"));
            }
            else {
                address_object.add(object.getString("address"));
            }
            description_object.add(object.getString("description"));
            date_object.add(object.getString("created_at"));
            status_object.add(object.getString("qualified_status"));
            lat_object.add(object.getString("lat"));
            lon_object.add(object.getString("lng"));
        }

        Img = img_object.toArray(new String[img_object.size()]);
        Type = type_object.toArray(new String[type_object.size()]);
        Address = address_object.toArray(new String[address_object.size()]);
        Description =description_object.toArray(new String[description_object.size()]);
        Date = date_object.toArray(new String[date_object.size()]);
        Status = status_object.toArray(new String[status_object.size()]);
        Lat = lat_object.toArray(new String[lat_object.size()]);
        Lon = lon_object.toArray(new String[lon_object.size()]);
    }

    private void getData(final String str) {
        now_type = str;
        final Activity Main = this;
        final session Session = new session(getSharedPreferences(AppName, 0));

        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection connect = null;
                String query = "";
                try {
                    query = URLEncoder.encode(str, "utf-8");
                } catch (UnsupportedEncodingException ignored) {
                }

                try {
                    URL connect_url = new URL("https://api.denguefever.tw/breeding_source/?qualified_status="+query);
                    connect = (HttpsURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.addRequestProperty("Authorization", "Token " +Session.getData("token"));
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        parseData(sb.toString());
                        Session.setData("report", sb.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportList();
                                bindClick();

                                Date curDate = new Date(System.currentTimeMillis());
                                update_time = curDate.getTime();
                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                    else {
                        parseData(Session.getData("report"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportList();
                                bindClick();
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();

                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                }
                catch (Exception e) {
                    Log.i("test", e.toString());
                    try {
                        parseData(Session.getData("report"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportList();
                                bindClick();
                                Toast.makeText(Main, "確認網路連線以更新資料！", Toast.LENGTH_SHORT).show();

                                refresh = true;
                                Main.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    } catch (JSONException ignored) {
                    }
                }
                finally {
                    if (connect != null) {
                        connect.disconnect();
                    }
                }
            }
        };
        thread.start();
    }

    private void getNumber(final String str) {
        final Activity Main = this;
        final session Session = new session(getSharedPreferences(AppName, 0));

        Thread thread = new Thread() {
            public void run() {
                HttpsURLConnection connect = null;
                String query = "";
                try {
                    query = URLEncoder.encode(str, "utf-8");
                } catch (UnsupportedEncodingException ignored) {
                }

                try {
                    URL connect_url = new URL("https://api.denguefever.tw/breeding_source/total/?qualified_status="+query);
                    connect = (HttpsURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.addRequestProperty("Authorization", "Token " +Session.getData("token"));
                    connect.connect();

                    int responseCode = connect.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();

                        JSONObject object = new JSONObject(sb.toString());
                        number = Integer.valueOf(object.getString("total"));
                        Session.setData("report_number", String.valueOf(number));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportListNumber();
                            }
                        });
                    }
                    else {
                        number = Integer.valueOf(Session.getData("report_number"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportListNumber();
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    number = Integer.valueOf(Session.getData("report_number"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reportListNumber();
                            Toast.makeText(Main, "確認網路連線以更新資料！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                finally {
                    if (connect != null) {
                        connect.disconnect();
                    }
                }
            }
        };
        thread.start();
    }
}
