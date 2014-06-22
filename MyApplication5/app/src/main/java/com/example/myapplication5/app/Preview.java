package com.example.myapplication5.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.List;

import BussinessLogic.Abstract.ICameraPreview;

/**
 * Created by Solomiia on 4/13/14.
 */
public class Preview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private byte[] mBuffer;
    private ICameraPreview _cameraPreview;

    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Preview(Context context) {
        super(context);
        init();
    }

    public void getInstance(ICameraPreview cameraPreview)
    {
        _cameraPreview = cameraPreview;
    }

    public void init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public SurfaceHolder GetHolder(){
        return mHolder;
    }

    protected void setDisplayOrientation(Camera camera, int angle){
        Method downPolymorphic;
        try
        {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where to draw.
        try {
            if (mCamera == null) {
                mCamera = Camera.open(); // WARNING: without permission in Manifest.xml, crashes
            }
        }
        catch (RuntimeException exception) {
            //Log.i(TAG, "Exception on Camera.open(): " + exception.toString());
            Log.d("", "Camera broken, quitting <img src='http://adblogcat.com/wp-includes/images/smilies/icon_sad.gif' alt=':(' class='wp-smiley'> ");

        }

        try {
            setDisplayOrientation(mCamera, 90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            //updateBufferSize();
            mCamera.addCallbackBuffer(mBuffer); // where we'll store the image data
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                public synchronized void onPreviewFrame(byte[] data, Camera c) {

                    if (mCamera != null) {
                        mCamera.addCallbackBuffer(mBuffer);
                    }
                }
            });
        } catch (Exception exception) {
            releaseCamera();
        }
    }

    public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        try {
            mParameters = mCamera.getParameters();

            List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();


            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            // older phone - doesn't support these calls
        }

        updateBufferSize();

        Camera.Size p = mCamera.getParameters().getPreviewSize();
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;

    }



    public byte[] getPic(byte[] data, int x, int y, int width, int height) {

        Bitmap source =  BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        int w =  width;
        int h =  height;
        Bitmap newBitmap = Bitmap.createBitmap(source,x,y,w,h, matrix, true);

       //Bitmap newBitmap = Bitmap.createBitmap(source,x,y,width,height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG,100, stream);
        return stream.toByteArray();
    }

    private void updateBufferSize() {
        mBuffer = null;
        System.gc();
        // prepare a buffer for copying preview data to
        int h = mCamera.getParameters().getPreviewSize().height;
        int w = mCamera.getParameters().getPreviewSize().width;
        int bitsPerPixel =
                ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat());
        mBuffer = new byte[w * h * bitsPerPixel / 8];

    }
    public void setCameraFocus(Camera.AutoFocusCallback autoFocus){
        if (mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_AUTO) ||
                mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_MACRO)){
            mCamera.autoFocus(autoFocus);
        }
    }

    public void setFlash(boolean flash){
        Toast.makeText(Preview.this.getContext(), "Flash is: "+mParameters.getFlashMode(), Toast.LENGTH_LONG).show();
        if (flash){
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
        }
        else{
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
        }
    }

    public void setPicture()
    {
        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){
            public void onPictureTaken(byte[] imageData, Camera c){
                Log.d("PictureData", imageData.toString());
                _cameraPreview.getResult(imageData);
            }
        };
        mCamera.takePicture(null,null, mPictureCallback);
    }
}

