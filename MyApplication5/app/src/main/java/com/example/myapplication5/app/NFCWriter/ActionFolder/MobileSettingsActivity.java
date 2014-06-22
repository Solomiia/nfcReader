package com.example.myapplication5.app.NFCWriter.ActionFolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.myapplication5.app.NFCReader.ListViewAdapter;
import com.example.myapplication5.app.R;
import com.example.myapplication5.app.ReceivingData;
import com.google.gson.Gson;

import java.util.ArrayList;

import BussinessLogic.Common.SwipeDetector;
import Models.MobileSettingsModel;
import Models.SettingsModel;

/**
 * Created by Solomiia on 5/12/2014.
 */
public class MobileSettingsActivity extends Activity {

    ListView listView;
    String[] ClassesList = new String[] { "WIFI", "BLUETOOTH", "Airplane Mode", "Auto Rotation", "Launch Application" };
    Boolean[] toggles = new Boolean[5];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_tag_writer_mobile_settings_layout);
        listView = (ListView) this.findViewById(R.id.list);
        listView.setAdapter(new ListViewAdapter(this, new String[5], ClassesList, true));
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected() && swipeDetector.getAction() == SwipeDetector.Action.LR)
                {
                    CheckBox checkBox =(CheckBox)view.findViewById(R.id.check);

                    MobileSettingsModel mobileSettingsModel = new MobileSettingsModel();
                    ArrayList<MobileSettingsModel> array =new ArrayList<MobileSettingsModel>();

                    SparseBooleanArray checkedItems = listView.getCheckedItemPositions();

                    if (checkedItems!= null)
                    {
                        for (int i = 0; i<checkedItems.size(); i++)
                        {
                            if (checkedItems.valueAt(i))
                            {
                               mobileSettingsModel = new MobileSettingsModel();
                               mobileSettingsModel.Mode = Boolean.toString(toggles[i]);
                               mobileSettingsModel.Choice = ClassesList[i];

                               array.add(mobileSettingsModel);

                            }
                        }
                    }
                    SettingsModel model = new SettingsModel();
                    model.mobileSettingsModels = array;

                    Intent myIntent = new Intent(MobileSettingsActivity.this, ReceivingData.class);
                    myIntent.putExtra("Activity","Writer");
                    myIntent.putExtra("SettingsModel",new Gson().toJson(model));
                    startActivityForResult(myIntent, 1);



                }
                else {
                    ToggleButton toggle = (ToggleButton)findViewById(R.id.togglebutton);
                    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                toggles[position] = isChecked;
                        }
                    });
                }
            }
        });

    }
}
