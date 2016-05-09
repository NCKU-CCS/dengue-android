package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nana on 2016/4/27.
 */
public class BreedingSource extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    private TelephonyManager TelManager;
    private session Session = new session();
    private gps Gps = new gps();
    private GoogleApiClient client;

    private final int CAMERA = 66;
    private Uri photoUri;
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private String[] types = new String[] {"住家積水容器", "雜物堆積(髒亂)", "空地積水容器"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breedingsource);

        TelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient();
        Session.setSession(getSharedPreferences(AppName, 0));
        Gps.set(new Geocoder(this, Locale.TRADITIONAL_CHINESE));

        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);

        mImg = (ImageView) findViewById(R.id.breedingSources_img);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, filename);
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, CAMERA);

        breedingSourcesSubmitTypeList();
        breedingSourcesSubmitSubmit();

        new menu(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA)  {
            //取得照片路徑uri
            Uri uri = null;
            if (data==null) {
                if (photoUri != null) {
                    uri = photoUri;
                }
            }
            else
            {
                uri = data.getData();
            }
            ContentResolver cr = this.getContentResolver();

            try {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap, mPhone.heightPixels);
                else ScalePic(bitmap, mPhone.widthPixels);
            } catch (FileNotFoundException e) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ScalePic(Bitmap bitmap, int phone) {
        //縮放比例預設為1
        float mScale = 1;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if (bitmap.getWidth() > phone) {
            //判斷縮放比例
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

    private void breedingSourcesSubmitTypeList() {
        //Spinner breedingSources_submit_spinner = (Spinner)findViewById(R.id.breedingSources_submit_type_value);
        //breedingSources_submit_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types));
    }

    private void breedingSourcesSubmitSubmit() {
        Button breedingSources_submitButton = (Button)findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                EditText description = (EditText) findViewById(R.id.breedingSources_submit_description_value);
                // type = (Spinner) findViewById(R.id.breedingSources_submit_type_value);
                String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.TRADITIONAL_CHINESE)
                        .format(new Date(System.currentTimeMillis()));

                //TODO: need to submit data to server
                //TextView output = (TextView) findViewById(R.id.breedingSources_submit_output);
                /*String data = "";
                data += "region=tainan&table=api_breedingsource";
                data += "id: " + Session.getStringData("phone") + "\n";
                data += "type:" + type.getSelectedItem().toString() + "\n";
                data += "description: " + description.getText().toString() + "\n";
                data += "date: " + now + "\n";
                data += "lat:" + Lat + "\n";
                data += "lon:" + Lon + "\n";
                data += "address:" + gps.get(Lat, Lon) + "\n";
                data += "done: false";
                output.setText(data);*/
            }
        });
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
        }
        //Log.i(AppName, "(" + Lon + "," + Lat + ") Connected!!!!!");

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
    // HTTP POST request
    public void sendPost(final String data){
        Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/breeding_source/insert//";
                HttpURLConnection con = null;
                Log.i(AppName, url);
                try {
                    URL connect_url = new URL(url);
                    con = (HttpURLConnection) connect_url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/form-data");
                    con.connect();
                    Log.i(AppName, data);

                    OutputStream output = con.getOutputStream();
                    output.write(data.getBytes());
                    output.flush();
                    output.close();

                    int responseCode = con.getResponseCode();
                    Log.i(AppName, String.valueOf(responseCode));

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
}
