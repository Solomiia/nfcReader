package com.example.myapplication5.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import BussinessLogic.Abstract.ICameraPreview;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.ParsingPhoto.FilterImage;
import BussinessLogic.ParsingPhoto.Wait;
import Models.TagInformation;

/**
 * Created by Solomiia on 4/10/2014.
 */
public class TakePicture extends Activity implements SensorEventListener, ICameraPreview {

    private DataHelper _dataHelper;
    private FilterImage _filterImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Preview mPreview;
    private ImageView mTakePicture;
    private TouchView mView;

    private boolean mAutoFocus = true;

    private boolean mFlashBoolean = false;

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mInitialized = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private Rect rec = new Rect();

    private int mScreenHeight;
    private int mScreenWidth;

    private boolean mInvalidate = false;

    private File mLocation;
    ImageView takePicture;


    public TakePicture(){
        _dataHelper = new DataHelper();
        _filterImage = new FilterImage();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // display our (only) XML layout - Views already ordered

        setContentView(R.layout.take_picture_page);

        if( !_dataHelper.isFeatureAvailable(this, PackageManager.FEATURE_CAMERA))
        {
            //save data as it is
            return;
        }


        // the accelerometer is used for autofocus
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // get the window width and height to display buttons
        // according to device screen size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        mScreenWidth = displaymetrics.widthPixels;
        // I need to get the dimensions of this drawable to set margins
        // for the ImageView that is used to take pictures

        mTakePicture = (ImageView) findViewById(R.id.startcamerapreview);

        rec.set((int)((double)mScreenWidth*.15), (int)((double)mScreenHeight*.10) , (int)((double)mScreenWidth*.15),
                (int)((double)mScreenHeight*.70));

        mTakePicture.setClickable(true);
        mTakePicture.setOnClickListener(previewListener);

        // get our Views from the XML layout
        mPreview = (Preview) findViewById(R.id.preview);
        mView = (TouchView) findViewById(R.id.left_top_view);
        takePicture = (ImageView) findViewById(R.id.camerapicture);

        mView.setRec(rec);
        mPreview.getInstance(this);




    }

    // this is the autofocus call back
    private Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

        public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
            //Wait.oneSec();
            mAutoFocus = true;
        }
    };

    private View.OnClickListener previewListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //if (mAutoFocus){
            mAutoFocus = false;
            mPreview.setFlash(false);
            mPreview.setCameraFocus(myAutoFocusCallback);
            Wait.oneSec();

            mPreview.setPicture();


//todo: get data and change visibility
        }
    };

    private void saveFile(byte[] bm)
    {
        String imageName = "IMG_"+ new Date().getTime()+".jpg";
        mLocation = new File(this.getFilesDir(),imageName);
        try {
            FileOutputStream  fOut = new FileOutputStream(mLocation);
            fOut.write(bm);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showCreatedImage(this.getFilesDir()+ "/" + imageName);
    }

    // just to close the app and release resources.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

      private void showCreatedImage(String path)
    {
        Uri uri = Uri.parse(path);

        System.gc();
        String value = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("result");

        }

        TagInformation tagInformation = new Gson().fromJson(value, TagInformation.class);

        tagInformation.ImagePath  = uri.toString();


        Intent myIntent = new Intent(TakePicture.this, PictureReceiver.class);
        myIntent.putExtra("result",new Gson().toJson(tagInformation));

        startActivityForResult(myIntent, 1);

    }



    public void onSensorChanged(SensorEvent event) {

        if (mInvalidate == true){
            mView.invalidate();
            mInvalidate = false;
        }
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX  = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaY > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
            mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }
        if (deltaZ > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
           mAutoFocus = false;
            mPreview.setCameraFocus(myAutoFocusCallback);
        }

        mLastX = x;
        mLastY = y;
        mLastZ = z;

    }

    // extra overrides to better understand app lifecycle and assist debugging
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause()");

        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.gc();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
        //Log.i(TAG, "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart()");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void getResult(byte[] data) {

        saveFile(mPreview.getPic(data, 180, 10, 300, 470));

    }
}
