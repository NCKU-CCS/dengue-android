package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Random;

public class BreedingSourceSubmit extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    private String type = "";
    private String description;
    private String address_user;
    private String address_gps;
    private boolean isFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.breedingsource);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        buildGoogleApiClient();
        breedingSourcesSubmitTypeList();

        new menu(this, 2);
        new goBack(this);
    }

    private void alert() {
        final Activity Main = this;

        Random r = new Random();
        int choice = r.nextInt(100);
        switch(choice % 3) {
            case 0:
                AlertDialog dialog1 = dialog(R.layout.alert_msg_01, Main);
                dialog1.show();
                dialog1.getWindow().setLayout(740, 740);
                break;
            case 1:
                AlertDialog dialog2 = dialog(R.layout.alert_msg_02, Main);
                dialog2.show();
                dialog2.getWindow().setLayout(740, 740);
                break;
            case 2:
                AlertDialog dialog3 = dialog(R.layout.alert_msg_03, Main);
                dialog3.show();
                dialog3.getWindow().setLayout(740, 740);
                break;
        }
    }

    private AlertDialog dialog(int layout, Activity Main) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main);
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                uploadSuccess();
            }
        });

        return dialog;
    }

    private void breedingSourcesSubmitTypeList() {
        final Button btn_hb = (Button)findViewById(R.id.button_hb);
        final Button btn_ob = (Button)findViewById(R.id.button_ob);

        btn_hb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_hb.setBackgroundResource(R.drawable.breeding_source_type_button_clicked_border);
                btn_hb.setTextColor(Color.parseColor("#ffffff"));
                btn_ob.setBackgroundResource(R.drawable.breeding_source_type_button_border);
                btn_ob.setTextColor(Color.parseColor("#000000"));
                type = "室內";
            }
        });

        btn_ob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_ob.setBackgroundResource(R.drawable.breeding_source_type_button_clicked_border);
                btn_ob.setTextColor(Color.parseColor("#ffffff"));
                btn_hb.setBackgroundResource(R.drawable.breeding_source_type_button_border);
                btn_hb.setTextColor(Color.parseColor("#000000"));
                type = "戶外";
            }
        });
    }

    private void loadBitmap(String url,int degree) {
        DisplayMetrics mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            rotate(bitmap,degree);
        } catch (Exception e) {
            Toast.makeText(this, "無法取得照片!", Toast.LENGTH_SHORT).show();
        }
    }

    private void rotate(Bitmap bitmap,int degree){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        ImageView mImg = (ImageView) findViewById(R.id.breedingSources_img);
        mImg.setImageBitmap(rotatedBMP);
    }

    private void breedingSourcesSubmitSubmit(final String imgUri) {
        final Activity Main = this;

        final TextView address_user_text = (TextView)findViewById(R.id.breedingSources_submit_address_user_value);
            address_user_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(address_user_text
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    if(address_user_text.getText().toString().equals(""))
                        address_user_text.setText(address_gps);

                    return true;
                }
                return false;
            }
        });
        final TextView description_text = (TextView)findViewById(R.id.breedingSources_submit_description_value);
        description_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(description_text
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        Button breedingSources_submitButton = (Button)findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(description_text
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (isFinish) {
                    address_user = address_user_text.getText().toString();
                    description = description_text.getText().toString();
                    sendImg(imgUri);
                    isFinish = false;
                } else {
                    Toast.makeText(Main, "請耐心等待!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendImg(final String img) {
        final session Session = new session(getSharedPreferences(AppName, 0));
        final Activity Main = this;

        Toast.makeText(Main, "等候上傳中...", Toast.LENGTH_SHORT).show();

        final Thread thread = new Thread() {
            public void run() {
                String query = "";
                try {
                    query = URLEncoder.encode("待審核", "utf-8");
                } catch (UnsupportedEncodingException ignored) {
                }

                String url = "https://api.denguefever.tw/breeding_source/?qualified_status=" + query;
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPostRequest = new HttpPost(url);
                File f = new File(img);

                try {
                    MultipartEntity multiPartEntityBuilder = new MultipartEntity();

                    multiPartEntityBuilder.addPart("photo", new FileBody(f));
                    multiPartEntityBuilder.addPart("source_type", new StringBody(type, Charset.forName("UTF-8")));
                    multiPartEntityBuilder.addPart("lng", new StringBody(String.valueOf(Lon)));
                    multiPartEntityBuilder.addPart("lat", new StringBody(String.valueOf(Lat)));
                    multiPartEntityBuilder.addPart("description", new StringBody(description, Charset.forName("UTF-8") ));
                    multiPartEntityBuilder.addPart("modified_address", new StringBody(address_user, Charset.forName("UTF-8")));

                    httpPostRequest.setHeader("Authorization", "Token " +Session.getData("token"));
                    httpPostRequest.setEntity(multiPartEntityBuilder);
                }
                catch (IOException ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "上傳失敗！請確認資料皆有填寫", Toast.LENGTH_SHORT).show();
                            isFinish = true;
                        }
                    });
                }

                try {
                    HttpResponse httpResponse = httpClient.execute(httpPostRequest);

                    int status = httpResponse.getStatusLine().getStatusCode();
                    Log.i("test", String.valueOf(status));
                    if(status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "上傳成功！", Toast.LENGTH_SHORT).show();
                                isFinish = true;

                                alert();
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "上傳失敗！請確認資料皆有填寫", Toast.LENGTH_SHORT).show();
                                isFinish = true;
                            }
                        });
                    }
                } catch (IOException ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main, "上傳失敗！請確認網路連線", Toast.LENGTH_SHORT).show();
                            isFinish = true;
                        }
                    });
                }
            }
        };
        thread.start();
    }

    public void uploadSuccess(){
        Intent intent = new Intent();
        intent.setClass(BreedingSourceSubmit.this, BreedingSourceSeparator.class);
        startActivity(intent);
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Lat = mLastLocation.getLatitude();
            Lon = mLastLocation.getLongitude();
            Bundle imgBundle = getIntent().getExtras();

            if (imgBundle != null && imgBundle.getString("img") != null) {
                String img = imgBundle.getString("img");
                int degree = imgBundle.getInt("degree");

                if(img != null) {
                    loadBitmap(img,0);
                    breedingSourcesSubmitSubmit(img);

                    gps Gps = new gps(this);
                    address_gps = Gps.get(Lat, Lon);
                    if(address_gps == null) {
                        address_gps = "";
                    }

                    EditText address_text = (EditText) findViewById(R.id.breedingSources_submit_address_user_value);
                    address_user = address_gps;
                    address_text.setText(address_user);
                }
            }
        }
        else {
            Toast.makeText(this, "請打開定位", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i("dengue", "無法連接google play！" );
        Toast.makeText(this, "無法連接google play！", Toast.LENGTH_SHORT).show();
    }
}

