package com.example.myapplication5.app.MapLocator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication5.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Solomiia on 5/21/2014.
 */
public class MapFragmentClass extends SupportMapFragment {

    GoogleMap mapView;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1,
                             Bundle arg2) {
//        return mInflater.inflate(R.layout.locator_support_map_fragment, arg1, false);
        return super.onCreateView(mInflater, arg1, arg2);
    }

    @Override
    public void onInflate(Activity arg0, AttributeSet arg1, Bundle arg2) {
        super.onInflate(arg0, arg1, arg2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapView = getMap();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(true);
        markerOptions.position(new LatLng(23.231251f, 71.648437f));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        mapView.addMarker(markerOptions);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
}
