package com.example.myapplication5.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication5.app.NFCReader.NfcReaferActivity;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import BussinessLogic.SqlLiteHelper.MySqlLiteHelper;
import BussinessLogic.SqlLiteHelper.SqlImplementation;
import Models.TagInformation;

/**
 * Created by Solomiia on 4/24/2014.
 */
public class PictureReceiver extends Activity {

    private SqlImplementation _sqlImplementation;
    private MySqlLiteHelper _mySqlLiteHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_page);

       TagInformation value = new TagInformation();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = new Gson().fromJson(extras.getString("result"), TagInformation.class);

        }

        Bitmap bm = BitmapFactory.decodeFile(value.ImagePath);

        ImageView imageView = (ImageView)findViewById(R.id.camerapicture);
        imageView.setImageBitmap(bm);


        Button btnNewMessage = (Button) findViewById(R.id.takePicture);
        final TagInformation finalValue = value;
        btnNewMessage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                Intent myIntent = new Intent(PictureReceiver.this, TakePicture.class);
                myIntent.putExtra("result", new Gson().toJson(finalValue));
                startActivityForResult(myIntent, 1);
            }
        });

        Button btnMessages = (Button) findViewById(R.id.skip);
        btnMessages.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                //save data with out picture
                _sqlImplementation = new SqlImplementation(PictureReceiver.this);
                _mySqlLiteHelper = _sqlImplementation.returnSqlBaseInstance();


                RemoveImage(finalValue.ImagePath);

                finalValue.ImagePath = createPictureInInternalMemory();

                showSavedData(SaveData(finalValue));

            }
        });

        Button btnPreferences = (Button) findViewById(R.id.save);
        btnPreferences.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {

                _sqlImplementation = new SqlImplementation(PictureReceiver.this);
                _mySqlLiteHelper = _sqlImplementation.returnSqlBaseInstance();

                showSavedData(SaveData(finalValue));
            }
        });



    }

    private void RemoveImage(String imagePath) {
          if (imagePath == null) return;

        File file = new File(imagePath);
        file.delete();
    }

    private int SaveData(TagInformation information)
    {
        try {
            return _sqlImplementation.InsertIntoDataTableValues(information);
        }catch(Exception e)
        {
            Log.d("Exception", e.getMessage());
            return 0;
        }
    }

    private void showSavedData(int index){
        Intent myIntent = new Intent(PictureReceiver.this, NfcReaferActivity.class);
        myIntent.putExtra("index", index);
        startActivityForResult(myIntent, 1);

    }

    private String createPictureInInternalMemory(){
        Bitmap bitMap = BitmapFactory.decodeResource(getResources(),R.drawable.default_card);

        String fileName ="default_card.png";
        File  file = new File(this.getFilesDir(),fileName);
        try {


            if(file.exists())
            {
                return  file.getAbsolutePath();
            }
            FileOutputStream  fOut = new FileOutputStream(file);

            bitMap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            fOut.flush();

            fOut.close();

        } catch (FileNotFoundException e1) {

            e1.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

}
