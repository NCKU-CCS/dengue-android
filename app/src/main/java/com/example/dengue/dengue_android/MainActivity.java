package com.example.dengue.dengue_android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //take photo
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    // google api
    private static final String AppName = "Dengue";
    private GoogleApiClient mGoogleApiClient;
    private double Lat;
    private double Lon;
    // phone information
    private TelephonyManager TelManager;
    // view class;
    private goBack GoBack = new goBack(this);
    private session Session = new session();
    private gps Gps = new gps();
    private loginEvent Login = new loginEvent(this);
    private menuEvent Menu = new menuEvent(this);
    private reportListEvent ReportList = new reportListEvent(this);
    private reportListCheckEvent ReportListCheck = new reportListCheckEvent(this);
    private bittenByMosquito BittenByMosquito = new bittenByMosquito(this);
    private breedingSourcesPhotoEvent BreedingSourcesPhoto = new breedingSourcesPhotoEvent(this);
    private breedingSourcesSubmitEvent BreedingSourcesSubmit = new breedingSourcesSubmitEvent(this);
    private hotEvent Hot = new hotEvent(this);
    private hospitalEvent Hospital = new hospitalEvent(this);
    private hospitalInfoEvent HospitalInfo = new hospitalInfoEvent(this);
    private View mV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient();
        Session.setSession(getSharedPreferences(AppName, 0));
        Gps.set(new Geocoder(this, Locale.TRADITIONAL_CHINESE));

        Login.setLoginView(TelManager, Session, new Runnable() {
            @Override
            public void run() {
                final Runnable MenuEvent = this;
                Menu.setMenuView(Login.getIsVillageChief(), Session, new Runnable() {
                    // log out
                    @Override
                    public void run() {
                        Login.setLoginView(TelManager, Session, MenuEvent);
                    }
                }, new Runnable() {
                    // report list
                    @Override
                    public void run() {
                        final Runnable ReportListEvent = this;
                        ReportList.setReportListView(GoBack.run(MenuEvent), new Runnable() {
                            // report list check
                            @Override
                            public void run() {
                                ReportListCheck.setReportListCheckView(ReportList.getData(), GoBack.run(ReportListEvent), ReportListEvent);
                            }
                        });
                    }
                }, new Runnable() {
                    // bitten by mosquito buttonEvent
                    @Override
                    public void run() {
                        BittenByMosquito.setBittenByMosquitoView(TelManager, Lat, Lon, Gps, GoBack.run(MenuEvent));
                    }
                }, new Runnable() {
                    // breeding sources PhotoEvent
                    @Override
                    public void run() {
                        final Runnable BreedingSource = this;
                        BreedingSourcesPhoto.setBreedingSourcesPhotoView(TelManager, Lat, Lon, Gps, GoBack.run(MenuEvent),
                                new Runnable() {
                                    // breeding sources submitEvent
                                    @Override
                                    public void run() {
                                        BreedingSourcesSubmit.setBreedingSourcesSubmitView(TelManager, Lat, Lon, Gps, GoBack.run(BreedingSource));
                                    }
                                }
                        );
                    }
                }, new Runnable() {
                    // hot
                    @Override
                    public void run() {
                        Hot.setHotView(GoBack.run(MenuEvent));
                    }
                }, new Runnable() {
                    // hospital
                    @Override
                    public void run() {
                        final Runnable HospitalEvent = this;
                        Hospital.sethospitalView(GoBack.run(MenuEvent), new Runnable() {
                            // hospital info
                            @Override
                            public void run() {
                                HospitalInfo.setHospitalInfoView(Hospital.getData(), GoBack.run(HospitalEvent));
                            }
                        });
                    }
                });
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Lat = mLastLocation.getLatitude();
            Lon = mLastLocation.getLongitude();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone); //error1

        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if (requestCode == CAMERA || requestCode == PHOTO)  {
            //取得照片路徑uri
            Uri uri = null;
            mV = breedingSourcesPhotoEvent.getView();
            if (data == null || data.getData()==null) {
                Uri photoUri = breedingSourcesPhotoEvent.getUri();
                if (photoUri != null) {
                    uri = photoUri;
                    //Toast.makeText(mV.getContext(),"from:data"+ uri.getPath(), Toast.LENGTH_LONG).show();
                }
            }
            else {
                uri = data.getData();
                //Toast.makeText(mV.getContext(),"from:photo"+ uri.getPath(), Toast.LENGTH_LONG).show();
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
        mImg =breedingSourcesPhotoEvent.getImg();
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
}