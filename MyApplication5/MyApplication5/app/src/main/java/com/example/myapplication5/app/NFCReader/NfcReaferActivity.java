package com.example.myapplication5.app.NFCReader;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication5.app.R;
import com.google.gson.Gson;

import BussinessLogic.SqlLiteHelper.SqlImplementation;
import Models.TagInformation;

/**
 * Created by Solomiia on 5/6/2014.
 */
public class NfcReaferActivity extends FragmentActivity {


    private SqlImplementation _sqlImplementation;
    public static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sqlImplementation = new SqlImplementation(this);

        setContentView(R.layout.tag_information);

        ActionBar actionbar = getActionBar();

        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Bundle extras = getIntent().getExtras();
        int index = 0;
        if (extras!= null)
        {
            index = extras.getInt("index");
        }
        TagInformation tagInformation = _sqlImplementation.getTagInformation(index);

        SetTabListener(actionbar, "Technical Information", R.drawable.technical_information, new TechnicalInformationActivity(),new Gson().toJson(tagInformation));


        if (tagInformation.TagDetails!= null) {
            SetTabListener(actionbar, "Tag Details", R.drawable.details, new TagDetailsActivity(), new Gson().toJson(tagInformation.TagDetails));
        }
        if(tagInformation.ndefMessageInformation!= null) {
            SetTabListener(actionbar, "Ndef Message Information", R.drawable.message, new NdefMessageInformationActivity(), new Gson().toJson(tagInformation.ndefMessageInformation));
        }


    }


    private void SetTabListener( ActionBar actionbar, String Text, int drawable, Fragment fragment, String data){
        View tabView = this.getLayoutInflater().inflate(R.layout.tab_layout, null);

        ActionBar.Tab tab = actionbar.newTab();
        tab.setCustomView(tabView);
       // TextView tabTxt = (TextView) tabView.findViewById(R.id.tab_title);
       // tabText.setText(Text);

        ImageView tabImage = (ImageView) tabView.findViewById(R.id.tab_icon);
        tabImage.setImageDrawable(this.getResources().getDrawable(drawable));


        Fragment _fragment = fragment;
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        _fragment.setArguments(bundle);

        tab.setTabListener(new MyTabsListener(_fragment));
        actionbar.addTab(tab);
    }

}

