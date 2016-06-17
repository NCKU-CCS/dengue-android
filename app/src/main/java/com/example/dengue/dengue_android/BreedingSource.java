package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BreedingSource extends Activity {
    private int rotate = 0;
    private SurfaceView sfv_preview;
    private Camera camera = null;
    private SurfaceHolder.Callback cpHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopPreview();
        }
    };
    private boolean check_photo = false;
    private String path;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);
        bindViews();
        new menu(this, 2);
    }

    private void bindViews() {
        sfv_preview = (SurfaceView) findViewById(R.id.sfv_preview);
        sfv_preview.getHolder().addCallback(cpHolderCallback);
        final Activity Main = this;

        final Button btn_take = (Button) findViewById(R.id.btn_take);
        final TextView choice = (TextView) findViewById(R.id.take_photo_choice);

        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_photo) {
                    btn_take.setText("");
                    choice.setText("取消");
                    check_photo = false;

                    Bundle bundle = new Bundle();
                    bundle.putString("img", path);
                    bundle.putInt("degree", rotate);

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(BreedingSource.this, BreedingSourceSubmit.class);
                    startActivity(intent);
                } else {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if ((path = saveFile(compressImageByQuality(data))) != null) {
                                btn_take.setText("確定");
                                choice.setText("重拍");
                                check_photo = true;
                            } else {
                                Toast.makeText(BreedingSource.this, "保存照片失敗", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });

        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_photo) {
                    btn_take.setText("");
                    choice.setText("取消");
                    check_photo = false;
                    camera.startPreview();
                }
                else {
                    Main.onBackPressed();
                }
            }
        });
    }

    public static byte[] compressImageByQuality(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        int options = 50;

        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baas);
        while (baas.toByteArray().length / 1024 > 300) {
            baas.reset();
            options -= 10;
            if(options < 0) options = 0;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baas);
            if(options == 0) break;
        }
        return baas.toByteArray();
    }

    private String saveFile(byte[] bytes){
        try {
            File file = File.createTempFile("img",".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void startPreview(){
        camera = Camera.open();
        try {
            int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 90; break;
                case Surface.ROTATION_90: degrees = 0; break;
                case Surface.ROTATION_180: degrees = 270; break;
                case Surface.ROTATION_270: degrees = 180; break;
            }

            camera.setPreviewDisplay(sfv_preview.getHolder());
            camera.setDisplayOrientation(degrees);
            rotate = degrees;
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
