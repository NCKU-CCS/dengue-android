package com.example.dengue.dengue_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //take photo
    private final int CAMERA = 66;
    private final int PHOTO = 99;
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
    private bittenByMosquitoEvent BittenByMosquito = new bittenByMosquitoEvent(this);
    private breedingSourcesPhotoEvent BreedingSourcesPhoto = new breedingSourcesPhotoEvent(this);
    private breedingSourcesSubmitEvent BreedingSourcesSubmit = new breedingSourcesSubmitEvent(this);
    private hotEvent Hot = new hotEvent(this);
    private hospitalEvent Hospital = new hospitalEvent(this);
    private hospitalInfoEvent HospitalInfo = new hospitalInfoEvent(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        buildGoogleApiClient();
        Session.setSession(getSharedPreferences(AppName, 0));
        Gps.set(new Geocoder(this, Locale.TRADITIONAL_CHINESE));

        Login.setLoginView(TelManager, Session, new Runnable() {
            @Override
            public void run() {
                final Runnable MenuEvent = this;
                Menu.setMenuView(Session, new Runnable() {
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
                        BittenByMosquito.setBittenByMosquitoView(Session, Lat, Lon, Gps, GoBack.run(MenuEvent));
                    }
                }, new Runnable() {
                    // breeding sources PhotoEvent
                    @Override
                    public void run() {
                        final Runnable BreedingSource = this;
                        BreedingSourcesPhoto.setBreedingSourcesPhotoView(CAMERA, PHOTO, GoBack.run(MenuEvent), new Runnable() {
                            // breeding sources submitEvent
                            @Override
                            public void run() {
                                BreedingSourcesSubmit.setBreedingSourcesSubmitView(Session, Lat, Lon, Gps, BreedingSourcesPhoto.getPic(), GoBack.run(BreedingSource));
                            }

                        });
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
    public void onBackPressed() {
        //返回鍵
        TextView Title = (TextView) findViewById(R.id.title);
        if(Title == null) {
            super.onBackPressed();
        }
        else if(Title.getText().toString().equals(getResources().getString(R.string.menu_title))){
            super.onBackPressed();
        }
        else {
            Runnable back = GoBack.getRunnable();
            back.run();
        }
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
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if (requestCode == CAMERA || requestCode == PHOTO)  {
            //取得照片路徑uri
            Uri uri = null;
            if (data == null || data.getData() == null) {
                Uri photoUri = breedingSourcesPhotoEvent.getUri();
                if (photoUri != null) {
                    uri = photoUri;
                }
            }
            else {
                uri = data.getData();
            }
            BreedingSourcesPhoto.setPic(uri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}