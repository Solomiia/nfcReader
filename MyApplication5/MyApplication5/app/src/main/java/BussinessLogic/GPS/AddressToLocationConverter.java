package BussinessLogic.GPS;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Models.Location;

/**
 * Created by Solomiia on 5/22/2014.
 */
public class AddressToLocationConverter extends AsyncTask<ArrayList<String>, Void, ArrayList<Models.Location>> {

    private Context context;


    public AddressToLocationConverter(Context context)
    {
        this.context = context;

    }


    @Override
    protected ArrayList<Models.Location> doInBackground(ArrayList<String>... params) {
       ArrayList<String> addressString = params[0];

       Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        Models.Location location = new Models.Location();



        List<Address> locations = null;
        ArrayList<Location> locationArrayList = new ArrayList<Location>(addressString.size());
        try {
            for(String address : addressString) {
                locations = geocoder.getFromLocationName(address, 1);

                Address loc = locations.get(0);

                location.Latitude = loc.getLatitude();
                location.Longitude = loc.getLongitude();
                locationArrayList.add(location);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return locationArrayList;
    }

}
