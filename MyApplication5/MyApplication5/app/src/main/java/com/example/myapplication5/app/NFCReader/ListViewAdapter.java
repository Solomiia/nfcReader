package com.example.myapplication5.app.NFCReader;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.myapplication5.app.R;

import BussinessLogic.Common.DataHelper;

/**
 * Created by Solomiia on 5/7/2014.
 */
public class ListViewAdapter extends BaseAdapter{

    Context context;
    String[] data;
    Integer[] images;
    String[] headers;
    boolean isCheckBoxList;

    private static LayoutInflater inflater =  null;

    public ListViewAdapter(Context context, String[] data, String[] headers)
        {
        this.context = context;
        this.data = data;
        this.headers = headers;
        this.images = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ListViewAdapter(Context context, String[] data, String[] headers, Integer[] images)
    {
        this.context = context;
        this.data = data;
        this.headers = headers;
        this.images = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ListViewAdapter(Context context, String[] data, String[] headers, boolean isCheckBoxList, Integer[] images)
    {
        this.context = context;
        this.data = data;
        this.headers = headers;
        this.images = images;
        this.isCheckBoxList = isCheckBoxList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ListViewAdapter(Context context, String[] data, String[] headers, boolean isCheckBoxList)
    {
        this.context = context;
        this.data = data;
        this.headers = headers;
        this.images = null;
        this.isCheckBoxList = isCheckBoxList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DataHelper dataHelper = new DataHelper();
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.tech_info_list_item, null);

        SetImageView(vi,position);
        SetCheckBox(vi);
        SetTextView(vi, position);
        return vi;
    }

    private void SetImageView(View vi, int position)
    {
        if (images == null) {
            return;
        }
        ImageView image = (ImageView)vi.findViewById(R.id.image);
        image.setImageResource(images[position]);
    }

    private void SetCheckBox(final View vi){
        CheckBox checkBox =(CheckBox)vi.findViewById(R.id.check);
        final  ToggleButton toggleButton = (ToggleButton)vi.findViewById(R.id.togglebutton);

        if (!isCheckBoxList) {
            checkBox.setVisibility(View.INVISIBLE);
            toggleButton.setVisibility(View.INVISIBLE);
            return;
        }


        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox) v;

                if (c.isChecked()) {

                    toggleButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    toggleButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void SetTextView(View vi,int position){

        TextView header = (TextView) vi.findViewById(R.id.header);
        header.setText(headers[position]);
        header.setTextColor(Color.WHITE);

        TextView text = (TextView) vi.findViewById(R.id.text);
        text.setText(data[position]);
        text.setTextColor(Color.WHITE);
    }
}
