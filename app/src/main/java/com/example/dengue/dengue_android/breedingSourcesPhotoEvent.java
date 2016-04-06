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
    private File tempFile;
    private ImageView mImg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 66;
    private final static int PHOTO = 99;
    private Uri photoUri;

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

    //@Override
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breeding_source_photo);
        this.tempFile = new File("/storage/external_SD/a.jpg");
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);
        mImg = (ImageView) findViewById(R.id.img);
        Button mCamera = (Button) findViewById(R.id.camera);
        Button mPhoto = (Button) findViewById(R.id.photo);
    }*/
    private void breedingSourcesTakePhoto() {
        //讀取手機解析度
        this.tempFile = new File("/storage/external_SD/a.jpg");
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone); //error1

        mImg = (ImageView)Main. findViewById(R.id.img);
        Button mCamera = (Button) Main.findViewById(R.id.camera);
        Button mPhoto = (Button) Main.findViewById(R.id.photo);

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                //value.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Video.Media.TITLE, filename);
                Toast.makeText(v.getContext(),"values::"+values, Toast.LENGTH_LONG).show();
                photoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values); //error2
                Toast.makeText(v.getContext(),"photoUri::"+photoUri, Toast.LENGTH_LONG).show();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA);

            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Main.startActivityForResult(intent, PHOTO);

            }
        });
    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO) && data != null) {
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
}
