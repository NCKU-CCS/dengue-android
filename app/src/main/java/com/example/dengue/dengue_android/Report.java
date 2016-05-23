package com.example.dengue.dengue_android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class report extends Activity {
    private CharSequence[] Id = new CharSequence[]{};
    private CharSequence[] Url = new CharSequence[]{};
    private CharSequence[] Type = new CharSequence[]{};
    private CharSequence[] Address = new CharSequence[]{};
    private CharSequence[] Description = new CharSequence[]{};
    private CharSequence[] Date = new CharSequence[]{};
    private CharSequence[] Status = new CharSequence[]{};

    private int number;
    private static final String AppName = "Dengue";
    private int now_choice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report);
        new menu(this, 4);
        getData();
    }

    private void reportListNumber() {
        TextView reportList_number = (TextView) findViewById(R.id.reportList_number);
        String output_number = null;
        switch (now_choice)
        {
            case 0:
                output_number = "您有 " + number + " 個待查點";
                break;
            case 1:
                output_number = "您有 " + number + " 個待處理點";
                break;
            case 2:
                output_number = "您有 " + number + " 個已處理點";
                break;
        }
        reportList_number.setText(output_number);
    }

    private void reportList(CharSequence[] id, CharSequence[] url, CharSequence[] type,
                            CharSequence[] address, CharSequence[] description, CharSequence[] date,
                            final CharSequence[] status) {
        final Activity Main = this;
        ListView report_list = (ListView) findViewById(R.id.reportList_list);
        report_list.setAdapter(new reportAdapter(this, id, url, type, address, description, date, status, Main));
        report_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                final Button check_yes = (Button) findViewById(R.id.reportList_check_yes);
                final Button check_wait = (Button) findViewById(R.id.reportList_check_wait);
                final Button check_no = (Button) findViewById(R.id.reportList_check_no);

                check_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(Id[position].toString(), "已處理");
                    }
                });
                check_wait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(Id[position].toString(), "通報處理");
                    }
                });
                check_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(Id[position].toString(), "非孳生源");
                    }
                });
            }
        });
    }

    private void bindClick() {
        final TextView report_not = (TextView) findViewById(R.id.reportList_type_not);
        final TextView report_wait = (TextView) findViewById(R.id.reportList_type_wait);
        final TextView report_done = (TextView) findViewById(R.id.reportList_type_done);

        report_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 0) {
                    now_choice = 0;
                    String[] token = new String[] {"未處理"};
                    filterData(token);
                    reportListNumber();

                    report_not.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                    report_wait.setBackgroundResource(R.drawable.hospital_choice_border);
                    report_done.setBackgroundResource(R.drawable.hospital_choice_border);
                }
            }
        });

        report_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 1) {
                    now_choice = 1;
                    String[] token = new String[]{"通報處理"};
                    filterData(token);
                    reportListNumber();

                    report_not.setBackgroundResource(R.drawable.hospital_choice_border);
                    report_wait.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                    report_done.setBackgroundResource(R.drawable.hospital_choice_border);
                }
            }
        });

        report_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now_choice != 2) {
                    now_choice = 2;
                    String[] token = new String[]{"已處理", "非孳生源"};
                    filterData(token);
                    reportListNumber();

                    report_not.setBackgroundResource(R.drawable.hospital_choice_border);
                    report_wait.setBackgroundResource(R.drawable.hospital_choice_border);
                    report_done.setBackgroundResource(R.drawable.hospital_choice_border_clicked);
                }
            }
        });
    }

    private void filterData(String[] token) {
        ArrayList<String> temp_Id = new ArrayList<>();
        ArrayList<String> temp_Url = new ArrayList<>();
        ArrayList<String> temp_Type = new ArrayList<>();
        ArrayList<String> temp_Address = new ArrayList<>();
        ArrayList<String> temp_Description  = new ArrayList<>();
        ArrayList<String> temp_Date  = new ArrayList<>();
        ArrayList<String> temp_Status = new ArrayList<>();

        for(int i = 0; i < Status.length; i++) {
            for (String aToken : token) {
                if (Status[i].toString().equals(aToken)) {
                    temp_Id.add(Id[i].toString());
                    temp_Url.add(Url[i].toString());
                    temp_Type.add(Type[i].toString());
                    temp_Address.add(Address[i].toString());
                    temp_Description.add(Description[i].toString());
                    temp_Date.add(Date[i].toString());
                    temp_Status.add(Status[i].toString());

                }
            }
        }

        CharSequence[] temp_id = temp_Id.toArray(new String[temp_Id.size()]);
        CharSequence[] temp_url = temp_Url.toArray(new String[temp_Url.size()]);
        CharSequence[] temp_type = temp_Type.toArray(new String[temp_Type.size()]);
        CharSequence[] temp_address = temp_Address.toArray(new String[temp_Address.size()]);
        CharSequence[] temp_description = temp_Description.toArray(new String[temp_Description.size()]);
        CharSequence[] temp_date = temp_Date.toArray(new String[temp_Date.size()]);
        CharSequence[] temp_status = temp_Status.toArray(new String[temp_Status.size()]);

        number = temp_id.length;
        reportListNumber();
        reportList(temp_id, temp_url, temp_type, temp_address, temp_description, temp_date, temp_status);
    }

    private void parseData(String data) throws JSONException {
        if(data.equals("")) {
            return;
        }

        JSONArray output = new JSONArray(data);
        ArrayList<String> id_object = new ArrayList<>();
        ArrayList<String> url_object = new ArrayList<>();
        ArrayList<String> type_object = new ArrayList<>();
        ArrayList<String> address_object = new ArrayList<>();
        ArrayList<String> description_object = new ArrayList<>();
        ArrayList<String> date_object = new ArrayList<>();
        ArrayList<String> status_object = new ArrayList<>();

        for(int i = 0; i < output.length(); i++){
            JSONObject object = new JSONObject(output.get(i).toString());
            id_object.add(object.getString("source_id"));
            url_object.add(object.getString("photo_url"));
            type_object.add(object.getString("source_type"));
            address_object.add(object.getString("address"));
            description_object.add(object.getString("description"));
            date_object.add(object.getString("created_at"));
            status_object.add(object.getString("status"));
        }

        Id = id_object.toArray(new String[id_object.size()]);
        Url = url_object.toArray(new String[url_object.size()]);
        Type = type_object.toArray(new String[type_object.size()]);
        Address = address_object.toArray(new String[address_object.size()]);
        Description =description_object.toArray(new String[description_object.size()]);
        Date = date_object.toArray(new String[date_object.size()]);
        Status = status_object.toArray(new String[status_object.size()]);
        number = Id.length;
    }

    private void getData() {
        final Activity Main = this;
        final session Session = new session(getSharedPreferences(AppName, 0));

        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection connect = null;
                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/breeding_source/get/?database=tainan&status=已處理,非孳生源,未處理,通報處理");
                    connect = (HttpURLConnection) connect_url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setRequestProperty("Cookie", Session.getData("cookie"));
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
                                reportListNumber();
                                reportList(Id, Url, Type, Address, Description, Date, Status);
                                bindClick();
                            }
                        });
                    }
                    else {
                        parseData(Session.getData("report"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportListNumber();
                                reportList(Id, Url, Type, Address, Description, Date, Status);
                                bindClick();
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    try {
                        parseData(Session.getData("report"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportListNumber();
                                reportList(Id, Url, Type, Address, Description, Date, Status);
                                bindClick();
                                Toast.makeText(Main, "確認網路連線以更新資料！", Toast.LENGTH_SHORT).show();
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

    public void updateData(String id, final String status) {
        final session Session = new session(getSharedPreferences(AppName, 0));
        final String data = "database=tainan&source_id="+id+"&status="+status;
        final Activity Main = this;

        Thread thread = new Thread() {
            public void run() {
                HttpURLConnection con = null;

                try {
                    URL connect_url = new URL("http://140.116.247.113:11401/breeding_source/update/");
                    con = (HttpURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    con.setRequestProperty("Cookie", Session.getData("cookie"));
                    con.connect();

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, status, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "無法連接資料庫！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "無法連接資料庫！請確認網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        };
        thread.start();
    }

}
