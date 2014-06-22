package com.example.myapplication5.app.NFCReader;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication5.app.R;
import com.google.gson.Gson;

import BussinessLogic.Common.DataHelper;
import Models.TagInformation;

/**
 * Created by Solomiia on 5/6/2014.
 */
public class TechnicalInformationActivity extends Fragment {

    TextView textView;
    ImageView imageView;
    TagInformation tagInformation;
    ListView listView;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        bundle = getArguments();

        return inflater.inflate(R.layout.technical_information_layout, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

       if (savedInstanceState == null)
       {
           savedInstanceState = bundle;
       }

        super.onActivityCreated(savedInstanceState);

        String value = "";
        tagInformation = new TagInformation();
        if(savedInstanceState != null){
            value = savedInstanceState.getString("data");
            tagInformation = new Gson().fromJson(value,TagInformation.class);
            FillInUi();
        }
     }

    private void FillInUi()
    {

        DataHelper _dataHelper = new DataHelper();
        imageView = (ImageView) getActivity().findViewById(R.id.tech_info_image);
        listView = (ListView) getActivity().findViewById(R.id.tech_info_list);

        Bitmap bm = BitmapFactory.decodeFile(tagInformation.ImagePath);
        imageView.setImageBitmap(bm);

        //todo: fill in tagInformation and its Value
        listView.setAdapter(new ListViewAdapter(this.getActivity(), _dataHelper.ToArray(tagInformation)[1], _dataHelper.ToArray(tagInformation)[0]));
    }
}
