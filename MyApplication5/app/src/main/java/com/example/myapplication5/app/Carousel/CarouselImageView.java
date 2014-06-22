package com.example.myapplication5.app.Carousel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Solomiia on 4/16/14.
 */
public class CarouselImageView extends ImageView
        implements Comparable<CarouselImageView> {

    private int index;
    private float currentAngle;
    private float x;
    private float y;
    private float z;
    private boolean drawn;

    public CarouselImageView(Context context) {
        this(context, null, 0);
    }

    public CarouselImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public int compareTo(CarouselImageView another) {
        return (int)(another.z - this.z);
    }


}