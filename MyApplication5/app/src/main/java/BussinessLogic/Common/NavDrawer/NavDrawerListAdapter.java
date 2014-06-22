package BussinessLogic.Common.NavDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication5.app.R;

import java.util.ArrayList;

/**
 * Created by Solomiia on 6/11/2014.
 */
public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    public static final String PREFS_NAME = "MyPrefsFile";

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems)
    {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }



    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (navDrawerItems.get(position).getExtra())
        {
           convertView =  getExtraView(position,convertView,parent);

            if (//todo checkbox) {
                setVisibilityToAdditionalTemplate(convertView, false);
            }
            return  convertView;

        }

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);

        }

        final ViewGroup row = parent;


        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        final CheckBox checkBox;

        if (navDrawerItems.get(position).getChecked()) {
             checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            if (position == 0)
                //todo check if there is cashed url
                checkBox.setChecked(true);
        }
        else {
             checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.INVISIBLE);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                if (checkBox.isChecked()) {
                    setCheckAllChildrenCascade(row, false);
                    checkBox.setChecked(true);
                    if (navDrawerItems.get(position+1).getExtra()){
                        final View view = row.getChildAt(position+1);
                        setVisibilityToAdditionalTemplate(view, true);

                       //todo: expand list view item to find source maps
                       Toast.makeText(context, "CheckBox " + position, Toast.LENGTH_SHORT).show();

                   }

                }
                else if (!checkBox.isChecked()) {

                    setCheckAllChildrenCascade(row, true);
                    checkBox.setChecked(false);

                    if (navDrawerItems.get(position+1).getExtra()){
                        final View view = row.getChildAt(position+1);
                        setVisibilityToAdditionalTemplate(view, false);
                    }
                }


            }
        });

        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        return convertView;
    }

    private void setVisibilityToAdditionalTemplate(View view, Boolean setVisible)
    {
        int data =setVisible ? View.VISIBLE: View.GONE;

        EditText editText = (EditText)view.findViewById(R.id.edit_text);
        editText.setVisibility(data);
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.image_button);
        imageButton.setVisibility(data);

        view.setVisibility(data);


    }
    private View getExtraView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null && navDrawerItems.get(position).getExtra()) {

            LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
               convertView = mInflater.inflate(R.layout.drawer_list_item_for_directory_path, null);

        }

        ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return  convertView;
    }


    private void setCheckAllChildrenCascade(ViewGroup vg, Boolean check) {
        for (int i = 0; i < 2; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox && ((CheckBox) v).isChecked() != check) {
                  ((CheckBox) v).setChecked(check);
            } else if (v instanceof ViewGroup) {
                setCheckAllChildrenCascade((ViewGroup) v, check);
            }
        }
    }





    private void savePathToCathe(String path)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("offlinePath", path);

        // Commit the edits!
        editor.commit();
    }

}
