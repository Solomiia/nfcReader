package com.example.myapplication5.app.NFCWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication5.app.NFCReader.ListViewAdapter;
import com.example.myapplication5.app.NFCWriter.ActionFolder.MobileSettingsActivity;
import com.example.myapplication5.app.NFCWriter.ActionFolder.TextActivity;
import com.example.myapplication5.app.R;

public class ActionQueryActivity  extends Activity implements ListView.OnItemClickListener {

    ListView listView;
    String[] ClassesList = new String[] { "Mobile Settings", "Email", "Message", "Phone number", "Url","Text"};
    Integer[] images = new Integer[] { R.drawable.mobile_settings,
                                       R.drawable.email,
                                       R.drawable.message2,
                                       R.drawable.phone_number,
                                       R.drawable.link,
                                       R.drawable.text};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_tag_writer_action_query_activity);

        listView = (ListView) this.findViewById(R.id.list);
        listView.setAdapter(new ListViewAdapter(this,ClassesList, new String[6], images));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        switch(position){
            case 0: {
                intent.setClass(this, MobileSettingsActivity.class);
                break;
            }
            case 1: {
                intent.setClass(this, TextActivity.class);
                intent.putExtra("Mode",ClassesList[position]);
                break;
            }
            case 2: {
                intent.setClass(this, TextActivity.class);
                intent.putExtra("Mode",ClassesList[position]);
                break;
            }
            case 3: {
                intent.setClass(this, TextActivity.class);
                intent.putExtra("Mode",ClassesList[position]);
                break;
            }
            case 4: {
                intent.setClass(this, TextActivity.class);
                intent.putExtra("Mode",ClassesList[position]);
                break;
            }
            case 5: {
                intent.setClass(this, TextActivity.class);
                intent.putExtra("Mode",ClassesList[position]);
                break;
            }
        }
       startActivity(intent);
    }
}
