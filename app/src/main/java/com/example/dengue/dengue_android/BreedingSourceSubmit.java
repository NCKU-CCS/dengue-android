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
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.breedingsource);
        buildGoogleApiClient();

        session Session = new session(getSharedPreferences(AppName, 0));
        loadBitmap(Session.getData("breadingSource_img"));

       /* Bundle b = getIntent().getExtras();
        String img=null;
        if (b!= null) {
            img = b.getString("uri");
            Toast.makeText(this, img, Toast.LENGTH_LONG).show();
            //loadBitmap(Session.getData(img));
            //Log.i(AppName, img);
        }*/
        breedingSourcesSubmitTypeList();

        new menu(this,2);
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
        session Session = new session(getSharedPreferences(AppName, 0));
        Uri uri = Uri.parse(Session.getData("breadingSource_img"));
        final String img = getRealPath(uri);
        Log.i("PhotoPath",img);

        Button breedingSources_submitButton = (Button)findViewById(R.id.breedingSources_submit_submit);
        breedingSources_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                if (type.equals("")) {
                    Log.i(AppName, "No type");
                }

                EditText descript = (EditText) findViewById(R.id.breedingSources_submit_description_value);
                ImageView mImg = (ImageView) findViewById(R.id.breedingSources_img);
                mImg.buildDrawingCache();
                Bitmap bitmap = mImg.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                ContentBody mimePart = new ByteArrayBody(baos.toByteArray(), "filename");


                String data = "database=tainan";
                data += "&photo=" + Base64.encodeToString(byteArray, Base64.DEFAULT);
                data += "&source_type=" + type;
                data += "&lng=" + Lon;
                data += "&lat=" + Lat;
                data += "&description=" + descript.getText().toString();
                description = descript.getText().toString();
                data += "&status=未處理";


                try {
                    sendImg(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //sendImg(byteArray);
                //sendImg(mimePart);
                //sendPost(data);
            }
        });
    }
    public void sendImg(final String img) throws IOException {
        final session Session = new session(getSharedPreferences(AppName, 0));

        final Thread thread = new Thread() {
            public void run() {

                String url = "http://140.116.247.113:11401/breeding_source/insert/";
                //CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                HttpConnectionParams.setSoTimeout(httpParams, 10000);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                //HttpClient httpClient = HttpClients.createDefault();
                //HttpClient httpClient = HttpClientBuilder.create().build();

                HttpPost httpPostRequest = new HttpPost(url);
                //File f = new File("storage/emulated/0/Pictures/1464101518083.jpg");
                File f = new File(img);

                try {
                    StringBody db = new StringBody("tainan");
                    FileBody imgFile = new FileBody(f);
                    StringBody sourceType = new StringBody(type);
                    StringBody lon = new StringBody(String.valueOf(Lon));
                    StringBody lat = new StringBody(String.valueOf(Lat));
                    StringBody dc = new StringBody(description);
                    StringBody status = new StringBody("未處理");

                    MultipartEntity multiPartEntityBuilder = new MultipartEntity();

                    multiPartEntityBuilder.addPart("database", db);
                    multiPartEntityBuilder.addPart("photo", imgFile);
                    multiPartEntityBuilder.addPart("source_type", sourceType);
                    multiPartEntityBuilder.addPart("lng", lon);
                    multiPartEntityBuilder.addPart("lat", lat);
                    multiPartEntityBuilder.addPart("description", dc);
                    multiPartEntityBuilder.addPart("status", status);

                   /* httpPostRequest.setHeader("Accept", "application/json");
                    httpPostRequest.setHeader("Content-type", "application/json");
                    httpPostRequest.setHeader("enctype", "multipart/form-data");
                    httpPostRequest.setHeader("accept-charset", "UTF-8");*/
                    httpPostRequest.setHeader("Cookie", Session.getData("cookie"));


                    httpPostRequest.setEntity(multiPartEntityBuilder);
                }
                catch (IOException ex) {
                    Log.i("Dengue", "you got error:" + ex.toString());
                }

                try {
                    HttpResponse httpResponse = httpClient.execute(httpPostRequest);

                    int status = httpResponse.getStatusLine().getStatusCode();
                    String str = httpResponse.getStatusLine().getReasonPhrase();
                    Log.i("Dengue:STATUS CODE", status+" "+str);

                } catch (IOException ex) {
                    Log.i("Dengue", "you got error: http" + ex.toString());
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
        return result;
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

