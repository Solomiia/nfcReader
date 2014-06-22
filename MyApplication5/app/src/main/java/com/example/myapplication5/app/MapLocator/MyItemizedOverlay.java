package com.example.myapplication5.app.MapLocator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

/**
 * Created by Solomiia on 6/17/2014.
 */
public class MyItemizedOverlay extends ItemizedOverlay {


    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context context;


    public MyItemizedOverlay(Drawable drawable, Context context)
    {
        super(boundCenterBottom(drawable));
        this.context = context;
    }


    public MyItemizedOverlay(Drawable drawable) {
        super(boundCenterBottom(drawable));
    }


    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }


    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        OverlayItem item = mOverlays.get(index);
        Toast.makeText(context, "Overlay is tapped "+ index, 1000).show();
        return true;
    }
}
