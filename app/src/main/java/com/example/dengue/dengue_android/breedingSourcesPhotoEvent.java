package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nana on 2016/3/30.
 */
public class breedingSourcesPhotoEvent extends Activity {
    //宣告
    private static ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    private static Uri photoUri;
    private View mV;

    private MainActivity Main;
    private TelephonyManager TelManager;
    private double Lat;
    private double Lon;
    private gps gps;
    private Runnable submit;


    breedingSourcesPhotoEvent(MainActivity mMain) {
        Main = mMain;

    }

    public void setBreedingSourcesPhotoView(TelephonyManager mTelManager,
                                            double mLat,
                                            double mLon,
                                            gps mGps,
                                            Runnable goBack,
                                            Runnable mSubmit
    ) {
        TelManager = mTelManager;
        Lat = mLat;
        Lon = mLon;
        gps = mGps;
        submit = mSubmit;
        Main.setContentView(R.layout.breeding_source_photo);

        goBack.run();
        breedingSourcesTakePhoto();
        //submit.run();
    }

    public static Uri getUri()
    {
        return photoUri;
    }
    public static ImageView getImg()
    {
        return mImg;
    }

    private void breedingSourcesTakePhoto() {
        //讀取手機解析度
        mPhone = new DisplayMetrics();
        Main.getWindowManager().getDefaultDisplay().getMetrics(mPhone); //error1

        mImg = (ImageView) Main.findViewById(R.id.img);
        Button mCamera = (Button) Main.findViewById(R.id.camera);
        Button mPhoto = (Button) Main.findViewById(R.id.photo);

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mV = v;
                //開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                //value.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Video.Media.TITLE, filename);
                photoUri = Main.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values); //error2
                //Toast.makeText(v.getContext(), photoUri.getPath(), Toast.LENGTH_LONG).show();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Main.startActivityForResult(intent, CAMERA);
                //takingPhoto(intent, CAMERA);

            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //Toast.makeText(v.getContext(), intent.getDataString(), Toast.LENGTH_LONG).show();

                Main.startActivityForResult(intent, PHOTO);
                //takingPhoto(intent, PHOTO);
            }
        });
    }
}
