package com.example.dengue.dengue_android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class breedingSourcesPhotoEvent {
    private MainActivity Main;
    private int CAMERA;
    private int PHOTO;
    private static Uri photoUri;
    private Bitmap pic;

    private Runnable BreedingSourcesSubmit;

    breedingSourcesPhotoEvent(MainActivity mMain) {
        Main = mMain;
    }

    public void setBreedingSourcesPhotoView(int mCAMERA,
                                            int mPHOTO,
                                            Runnable goBack,
                                            Runnable mBreedingSourcesSubmit
    ) {
        CAMERA = mCAMERA;
        PHOTO = mPHOTO;
        BreedingSourcesSubmit = mBreedingSourcesSubmit;
        Main.setContentView(R.layout.breeding_source_photo);

        goBack.run();
        breedingSourcesTakePhoto();
    }

    private void breedingSourcesTakePhoto() {
        Button Camera = (Button) Main.findViewById(R.id.breedingSources_photo_camera);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String filename = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.TRADITIONAL_CHINESE)
                        .format(new Date());
                ContentValues values = new ContentValues();
                //value.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.TITLE, filename);
                photoUri = Main.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //photoUri = Uri.parse("/sdcard/xxx.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Main.startActivityForResult(intent, CAMERA);
                //takingPhoto(intent, CAMERA);
            }
        });

        Button Photo = (Button) Main.findViewById(R.id.breedingSources_photo_photo);
        Photo.setOnClickListener(new View.OnClickListener() {
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

    public static Uri getUri() {
        return photoUri;
    }

    public void setPic(Uri uri) {
        DisplayMetrics Phone = new DisplayMetrics();
        Main.getWindowManager().getDefaultDisplay().getMetrics(Phone);
        ContentResolver cr = Main.getContentResolver();

        try {
            //讀取照片，型態為Bitmap
            assert uri != null;
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

            //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
            if (bitmap != null) {
                scalePic(bitmap, (
                        bitmap.getWidth() > bitmap.getHeight() ?
                                Phone.heightPixels :
                                Phone.widthPixels
                ));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void scalePic(Bitmap bitmap, int phone) {
        if(bitmap.getWidth() > phone) {
            //判斷縮放比例
            float Scale = (float) phone / (float) bitmap.getWidth();

            Matrix Mat = new Matrix();
            Mat.setScale(Scale, Scale);

            pic = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    Mat,
                    false);
        }
        else {
            pic = bitmap;
        }
        BreedingSourcesSubmit.run();
    }

    public Bitmap getPic() {
        return pic;
    }
}
