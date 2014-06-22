package com.example.myapplication5.app.MapLocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.myapplication5.app.Common.AlertDialogManager;
import com.example.myapplication5.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.Common.ExcelReader;
import BussinessLogic.Common.NavDrawer.NavDrawerItem;
import BussinessLogic.Common.NavDrawer.NavDrawerListAdapter;
import BussinessLogic.GPS.GeoLocator;
import BussinessLogic.GPS.GetAddressTask;
import Models.Atmosphera;

public class LocatorActivity extends SherlockFragmentActivity implements IAsyncTask, SearchView.OnQueryTextListener {


    AutoCompleteTextView textView;
    GoogleMap map;
    String query;
    String city;
    GeoLocator geoLocator;
    private Map<String, ArrayList<Atmosphera>> atmospheraBanks;

    private boolean isChanged;
    private DataHelper _dataHelper;
    private int Length = 100;
    ProgressDialog mDialog;

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;

    private ArrayList<OverlayItem> mOverlays;
    ArrayList<NavDrawerItem> navDrawerItems;
    Context context;
    private ActionBarDrawerToggle mDrawerToggle;
    AlertDialogManager alertManager;
    public NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.locator_activity_layout);

        _dataHelper =new DataHelper();
        alertManager = new AlertDialogManager(this);

        isChanged = false;
        query = "";
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.clear();

        context = this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerList = (ListView)findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        ArrayList<NavDrawerItem> navDrawerItems1 = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem("Use google maps",R.drawable.maps_wifi, true));
        navDrawerItems.add(new NavDrawerItem("Use build-in maps",R.drawable.maps_download, true));

        navDrawerItems.add(new NavDrawerItem("",0, false, true));

        navDrawerItems.add(new NavDrawerItem("Your bank",R.drawable.location));
        navDrawerItems.add(new NavDrawerItem("Group banks",R.drawable.suitable_location_icon));

        adapter = new NavDrawerListAdapter(this, navDrawerItems);
        mDrawerList.setAdapter(adapter);



        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.location,
                R.string.drawer_open,
                R.string.drawer_close) {


            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        enableGPSModule();
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        try {

            final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
            searchView.setQueryHint("Search");


            android.support.v7.widget.SearchView.SearchAutoComplete autoCompleteTextView = (android.support.v7.widget.SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

            if (autoCompleteTextView != null) {
                autoCompleteTextView.setDropDownBackgroundResource(R.drawable.abc_search_dropdown_light);
            }

            menu.add(Menu.NONE,Menu.NONE,1,"Search")
                    .setIcon(R.drawable.location)
                    .setActionView(searchView)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 0) {
                        // Search

                    } else {
                        // Do something when there's no input
                    }
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {

                    hideKeyboard();
                    Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                    setSupportProgressBarIndeterminateVisibility(true);

                    setValidLocation(query);

                    return false;
                }
            });

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                hideKeyboard();
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }


    private void setValidLocation(String query) {

        if (query == null || query.equals(""))
        {
            alertManager.onCreateDialog("Bank name", "Please enter search string");
            return;
        }
        if (city==null || city.equals("")) {
            Location location = geoLocator.getLocation();
            GetAddressTask addressTask = new GetAddressTask(this, this);
            city = addressTask.getCityName(location);
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

    private void enableGPSModule() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean enabledWifi = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        if (!enabledWifi)
        {
            Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
            startActivity(intent);
        }
        geoLocator = new GeoLocator(this, map);
        LocationListener locListener = geoLocator;
        service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);


    }

    public void getResult(String city)
    {
        this.city = city;

        if (city=="")
        {
            city = "Львів";
            return;
        }
        atmospheraBanks = getSupportedBanks(city);
        if (atmospheraBanks.size() > 0 && mDialog!= null) {

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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
