package BussinessLogic.GPS;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import BussinessLogic.Abstract.IAsyncTask;

/**
 * Created by Solomiia on 5/22/2014.
 */
public class GetAddressTask extends AsyncTask<Location, Void, String> {

    IAsyncTask listener;
    Context context;
    public GetAddressTask(Context context, IAsyncTask listener) {
        super();
        this.context = context;
        this.listener =listener;
    }

    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder =
                new Geocoder(context, Locale.getDefault());

        Location loc = params[0];

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
        } catch (IOException e1) {
            Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return ("");
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) + " , " + Double.toString(loc.getLongitude()) + " passed to address service";
            Log.e("LocationSampleActivity", errorString);
            e2.printStackTrace();
            return "";
        }

        if (addresses != null && addresses.size() > 0) {

            Address address = addresses.get(0);
            return address.getLocality();
        } else {
            return "";
        }

    }

    @Override
    protected  void onPostExecute(String result)
    {
        listener.getResult(result);
    }
}
