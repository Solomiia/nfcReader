package com.example.myapplication5.app.NFCReader;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.example.myapplication5.app.R;

/**
 * Created by Solomiia on 5/6/2014.
 */
public class MyTabsListener implements ActionBar.TabListener {

    public Fragment fragment;
    public MyTabsListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_container, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Toast.makeText(NfcReaferActivity.appContext, "Reselected!", Toast.LENGTH_LONG).show();
    }
}
