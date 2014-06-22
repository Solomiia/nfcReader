package BussinessLogic.GPS;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication5.app.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import BussinessLogic.Abstract.IAsyncTask;

/**
 * Created by Solomiia on 5/22/2014.
 */
public class GeoLocator extends Service implements LocationListener, LocationSource, IAsyncTask {

    private  Context context;
    GetAddressTask getAddressTask;
    private IAsyncTask _asyncTaskListener;

    protected LocationManager locationManager;

    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;


    private static final long MIN_TIME_BW_UPDATES = 400;

    GoogleMap map;

    public GeoLocator(Context context, GoogleMap map)
    {
        this.map = map;
        map.setLocationSource(this);
        this.context = context;
        _asyncTaskListener = (IAsyncTask)context;
        getLocation();

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

             isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
             isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                     && wifi.isWifiEnabled();

            if (!isGPSEnabled && !isNetworkEnabled) {
                return null;
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,  MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,   MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (locationManager != null) {
                      location = getLocationPosition();
                      onLocationChanged(location);
                    }
                }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }


    private Location getLocationPosition()
    {
        location = (isNetworkEnabled) ? locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) :locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            location.setAccuracy((float) 100);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            float accuracy =location.getAccuracy();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

        try {
            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
            moveCameraToCurrentPosition(coordinate);

            getAddressTask = new GetAddressTask(context,this);
            getAddressTask.execute(location);

        }catch(Exception e)
        {
            Log.e("Error",e.toString());
        }
   }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void moveCameraToCurrentPosition( LatLng location)
    {

        if (location == null) return;
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);

        map.moveCamera(center);
        map.animateCamera(zoom);

        setCurrentPosition(location);


    }


    private void setCurrentPosition(LatLng position)
    {

        map.addMarker(new MarkerOptions()
            .position(position)
            .title("Current Location(You)")
            .snippet("Current")

            .icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.current_location_icon))
            .draggable(true));






    }


    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GeoLocator.this);
        }
    }


    @Override
    public void getResult(String result) {
        _asyncTaskListener.getResult(result);

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }
}