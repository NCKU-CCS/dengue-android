package com.example.dengue.dengue_android;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.apache.http.entity.mime.content.StringBody;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class breedingSourceSubmit extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    private String type = "";
    private String description;
    private boolean isFinish = true;
    private boolean isGet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.breedingsource);
        buildGoogleApiClient();
        breedingSourcesSubmitTypeList();

        new menu(this, 2);
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
            Toast.makeText(this, "無法取得照片!", Toast.LENGTH_SHORT).show();
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

    private void breedingSourcesSubmitSubmit(String imgUri) {
        Uri uri = Uri.parse(imgUri);
        final String img = getRealPath(uri);
        final Activity Main = this;

        Button breedingSources_submitButton = (Button)findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                if(isFinish && isGet) {
                    EditText description_text = (EditText) findViewById(R.id.breedingSources_submit_description_value);
                    description = description_text.getText().toString();
                    sendImg(img);
                    isFinish = false;
                }
                else {
                    Toast.makeText(Main, "請耐心等待!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendImg(final String img) {
        final session Session = new session(getSharedPreferences(AppName, 0));
        final Activity Main = this;

        final Thread thread = new Thread() {
            public void run() {
                String url = "http://140.116.247.113:11401/breeding_source/insert/";
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPostRequest = new HttpPost(url);
                File f = new File(img);

                try {
                    MultipartEntity multiPartEntityBuilder = new MultipartEntity();

                    multiPartEntityBuilder.addPart("database", new StringBody("tainan"));
                    multiPartEntityBuilder.addPart("photo", new FileBody(f));
                    multiPartEntityBuilder.addPart("source_type", new StringBody(type));
                    multiPartEntityBuilder.addPart("lng", new StringBody(String.valueOf(Lon)));
                    multiPartEntityBuilder.addPart("lat", new StringBody(String.valueOf(Lat)));
                    multiPartEntityBuilder.addPart("description", new StringBody(description));
                    multiPartEntityBuilder.addPart("status", new StringBody("未處理"));

                    httpPostRequest.setHeader("Cookie", Session.getData("cookie"));
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
                    if(status == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main, "上傳成功！", Toast.LENGTH_SHORT).show();
                                isFinish = true;
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

    private String getRealPath(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        isGet = true;
        return result;
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
                if(img != null) {
                    loadBitmap(img);
                    breedingSourcesSubmitSubmit(img);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast.makeText(this, "無法連接google play！", Toast.LENGTH_SHORT).show();
    }
}

