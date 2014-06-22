package com.example.myapplication5.app.Carousel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication5.app.NFCReader.NfcReaferActivity;
import com.example.myapplication5.app.R;
import com.example.myapplication5.app.ReceivingData;

import BussinessLogic.ParsingPhoto.Carousel.Carousel;
import BussinessLogic.ParsingPhoto.Carousel.CarouselAdapter;
import BussinessLogic.ParsingPhoto.Carousel.CarouselItem;


public class CarouselActivity extends Activity {


    private CarouselImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carousel);
        Carousel carousel = (Carousel)findViewById(R.id.carousel);

        carousel.setOnItemClickListener(new CarouselAdapter.OnItemClickListener(){

            public void onItemClick(CarouselAdapter<?> parent, View view,
                                    int position, long id) {



                int index = ((CarouselItem)view).getKey();
                Intent myIntent = new Intent(CarouselActivity.this, NfcReaferActivity.class);
                myIntent.putExtra("index", index);
                startActivityForResult(myIntent, 1);

            }

        });
        Button button = (Button)findViewById(R.id.createNewOne);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CarouselActivity.this, ReceivingData.class);
                startActivityForResult(myIntent, 1);
            }
        });
    }

}
