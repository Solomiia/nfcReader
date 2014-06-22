package com.example.myapplication5.app.MapLocator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.myapplication5.app.NFCReader.ListViewAdapter;
import com.example.myapplication5.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.Common.ExcelReader;
import BussinessLogic.GPS.GeoLocator;
import Models.Atmosphera;


public class LocatorActivity extends Activity  implements IAsyncTask, SearchView.OnQueryTextListener{

    AutoCompleteTextView textView;
    ImageButton locator;
    ImageButton settings;
    GoogleMap map;
    String query;
    GeoLocator geoLocator;
    private Map<String, ArrayList<Atmosphera>> atmospheraBanks;

    private boolean isChanged;
    private DataHelper _dataHelper;
    private int Length = 100;
    ProgressDialog mDialog;

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ListView mDrawerSettingsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locator_activity_layout);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        _dataHelper =new DataHelper();
        isChanged = false;
        query = "";
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.clear();

        enableGPSModule();

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar);

        textView = (AutoCompleteTextView) findViewById(R.id.search);
        locator = (ImageButton) findViewById(R.id.locator);
        settings = (ImageButton) findViewById(R.id.settings);


        locator.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                setValidLocation();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

                mDrawerList = (ListView)findViewById(R.id.list_slidermenu);
                mDrawerSettingsList = (ListView)findViewById(R.id.list_settings_menu);

                //Context context, String[] data, String[] headers, boolean isCheckBoxList, Integer[] images

                mDrawerList.setAdapter(new ListViewAdapter(getBaseContext(), new String[]{"Use google maps","Use build-in maps"},new String[2], true, new Integer[]{R.drawable.maps_wifi, R.drawable.maps_download}));
                mDrawerSettingsList.setAdapter(new ListViewAdapter(getBaseContext(), new String[]{"Your bank", "Group banks"}, new String[2],new Integer[]{R.drawable.location, R.drawable.suitable_location_icon}));
            }
        });

        if (mDrawerList == null) return;

        mDrawerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void enableGPSModule() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        geoLocator = new GeoLocator(this, map);
        LocationListener locListener = geoLocator;
        service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);


    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void setValidLocation() {

        query = textView.getText().toString();
        if (query == null || query == "")
        {
           // _dataHelper.MessageBox("");
            return;
        }

        Set<String> keys = atmospheraBanks.keySet();

        setAdapter(keys);


        String key = _dataHelper.contains(keys, query);
        //todo: check to make more correcter selection key

        ArrayList<Atmosphera> data = atmospheraBanks.get(key);

        boolean isGrouped =!_dataHelper.StringNullOrEmpty(data.get(0).GroupName);
        Location location = geoLocator.getLocation();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        _dataHelper.Where(map, atmospheraBanks,Length, isGrouped, key, position);



    }

    private void setAdapter(Set<String> keys)
    {
        ArrayList<String> array = _dataHelper.toArrayList(keys);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.id.search, array);
        textView.setAdapter(adapter);
    }


    public void getResult(String city)
    {


        if (city=="")
        {
            //todo: show Message to ask to reboot phone
            return;
        }
        atmospheraBanks = getSupportedBanks(city);
        if (atmospheraBanks.size() > 0) {

            mDialog.dismiss();
            mDialog.cancel();
        }
    }
    private Map<String,ArrayList<Atmosphera>> getSupportedBanks(String city)
    {
        System.gc();
        if (atmospheraBanks != null) return atmospheraBanks;


        ExcelReader reader = new ExcelReader(this);
        return reader.readFromExcel(city);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.query = query;
        isChanged = true;
        setValidLocation();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.query = newText;
        isChanged = true;

        return false;
    }
}
