package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BreedingSourceSubmit extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.breedingsource);
        buildGoogleApiClient();

        session Session = new session(getSharedPreferences(AppName, 0));
        loadBitmap(Session.getData("breadingSource_img"));

        breedingSourcesSubmitTypeList();

        new menu(this);
        new goBack(this);
    }

    private void breedingSourcesSubmitTypeList() {
        final ImageView btn_hb = (ImageView)findViewById(R.id.button_hb);
        final ImageView btn_ob = (ImageView)findViewById(R.id.button_ob);
        final ImageView btn_og = (ImageView)findViewById(R.id.button_og);
        btn_hb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_ob.setImageResource(R.drawable.outdoor_bottle);
                btn_hb.setImageResource(R.drawable.home_bottle_onclick);
                btn_og.setImageResource(R.drawable.outdoor_grass);
                type = "住家容器";
            }
        });

        btn_ob.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                btn_ob.setImageResource(R.drawable.outdoor_bottle_onclick);
                btn_hb.setImageResource(R.drawable.home_bottle);
                btn_og.setImageResource(R.drawable.outdoor_grass);
                type = "戶外容器";
            }
        });
        btn_og.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_ob.setImageResource(R.drawable.outdoor_bottle);
                btn_hb.setImageResource(R.drawable.home_bottle);
                btn_og.setImageResource(R.drawable.outdoor_grass_onclick);
                type = "戶外髒亂處";
            }
        });
    }

    private void loadBitmap(String url) {
        Uri uri =  Uri.parse(url);
        ContentResolver cr = this.getContentResolver();
        DisplayMetrics mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

            if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap, mPhone.heightPixels);
            else ScalePic(bitmap, mPhone.widthPixels);
        } catch (FileNotFoundException e) {
            Log.i(AppName, "Not get image.");
        }
    }

    private void ScalePic(Bitmap bitmap, int phone) {
        ImageView mImg = (ImageView) findViewById(R.id.breedingSources_img);

        float mScale;
        if (bitmap.getWidth() > phone) {
            mScale = (float) phone / (float) bitmap.getWidth();

            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            mImg.setImageBitmap(mScaleBitmap);
        } else mImg.setImageBitmap(bitmap);
    }

    private void breedingSourcesSubmitSubmit() {
        Button breedingSources_submitButton = (Button)findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                if(type.equals("")) {
                    Log.i(AppName, "No type");
                }

                EditText description = (EditText) findViewById(R.id.breedingSources_submit_description_value);
                ImageView mImg = (ImageView) findViewById(R.id.breedingSources_img);
                mImg.buildDrawingCache();
                Bitmap bitmap = mImg.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();

                String data = "database=tainan";
                data += "&photo=" + Base64.encodeToString(byteArray, Base64.DEFAULT);
                data += "&source_type=" + type;
                data += "&lng=" + Lon;
                data += "&lat=" + Lat;
                data += "&description=" + description.getText().toString();
                data += "&status=未處理";

                sendPost(data);
            }
        });
    }

    public void sendPost(final String data){
        final session Session = new session(getSharedPreferences(AppName, 0));
        Log.i(AppName, data);
        Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/breeding_source/insert/";
                HttpURLConnection con = null;
                try {
                    URL connect_url = new URL(url);
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
                        Log.i(AppName, "post success");
                    } else {
                        Log.i(AppName, "post fail");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        };
        thread.start();
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
            breedingSourcesSubmitSubmit();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(AppName, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(AppName, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
}

