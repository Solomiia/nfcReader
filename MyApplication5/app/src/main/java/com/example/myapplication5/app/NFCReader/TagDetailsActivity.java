package com.example.myapplication5.app.NFCReader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication5.app.R;
import com.google.gson.Gson;

import BussinessLogic.Common.DataHelper;
import Models.TagDetails;

/**
 * Created by Solomiia on 5/6/2014.
 */
public class TagDetailsActivity extends Fragment {


    TagDetails tagDetails;
    ListView listView;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bundle = getArguments();
        return inflater.inflate(R.layout.tag_details_layout, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            savedInstanceState = bundle;
        }


        super.onActivityCreated(savedInstanceState);



        String value = "";
        tagDetails = new TagDetails();
        if (savedInstanceState != null) {
            value = savedInstanceState.getString("data");
            tagDetails = new Gson().fromJson(value, TagDetails.class);
        }

        FillInUi();

    }

    private void FillInUi() {

        DataHelper _dataHelper = new DataHelper();

        listView = (ListView) getActivity().findViewById(R.id.tech_info_list);
        listView.setAdapter(new ListViewAdapter(this.getActivity(), _dataHelper.ToArray(tagDetails)[1], _dataHelper.ToArray(tagDetails)[0]));
    }
}