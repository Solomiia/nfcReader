package com.example.myapplication5.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.myapplication5.app.Carousel.CarouselActivity;
import com.example.myapplication5.app.MapLocator.LocatorActivity;
import com.example.myapplication5.app.NFCWriter.ActionQueryActivity;

import BussinessLogic.SqlLiteHelper.SqlImplementation;

public class MainActivity extends Activity  {

        private SqlImplementation _sqlImplementation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _sqlImplementation = new SqlImplementation(this);
        final boolean isNotEmpty = _sqlImplementation.isTableNotEmpty();

        SharedPreferences settings =  getSharedPreferences("Activity", 0);
        if (settings!= null) {
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }

        Button btnNewMessage = (Button) findViewById(R.id.btn1);
        btnNewMessage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                if (isNotEmpty)
                {
                    Intent myIntent = new Intent(MainActivity.this, CarouselActivity.class);
                    startActivityForResult(myIntent, 1);
                }
                else {

                    Intent myIntent = new Intent(MainActivity.this, ReceivingData.class);
                    startActivityForResult(myIntent, 1);
                }
            }
        });

        Button btnMessages = (Button) findViewById(R.id.btn2);
        btnMessages.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                Intent myIntent = new Intent(MainActivity.this, LocatorActivity.class);
                startActivityForResult(myIntent, 1);
            }
        });

        Button btnPreferences = (Button) findViewById(R.id.btn3);
        btnPreferences.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                Intent myIntent = new Intent(MainActivity.this, ActionQueryActivity.class);
                startActivityForResult(myIntent, 1);
            }
        });

        Button btnFullVersion = (Button) findViewById(R.id.btn4);
        btnFullVersion.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
