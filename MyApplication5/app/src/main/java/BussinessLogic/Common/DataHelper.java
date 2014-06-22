package BussinessLogic.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;

import com.example.myapplication5.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Models.Atmosphera;
import Models.Location;
import Models.NdefMessageInformation;
import Models.TagDetails;
import Models.TagInformation;
import Models.TechTypes;

/**
 * Created by Solomiia on 4/10/2014.
 */
public class DataHelper<T> {


    String[] array = new String[] { "Text","Email", "Message", "Phone number", "Url"};



    public DataHelper(){

    }


    public void showInfoMessageDialog(String message,String title, Activity activity)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        alertDialog.create().show();
    }

    public boolean StringNullOrEmpty(String name)
    {
        return name == null || name == "";
    }

    public void Where (GoogleMap googleMap, Map<String, ArrayList<Atmosphera>> list, int length, boolean isGrouped,  String BankName, LatLng location) {
        if (list.size() == 0 || length == 0) return;

        Atmosphera data = list.get(BankName).get(0);
        Map<String, ArrayList<Atmosphera>> map = new HashMap<String, ArrayList<Atmosphera>>();
        String GroupName = data.GroupName;
        ArrayList<Atmosphera> array;

        if (!isGrouped) {
            array = list.get(BankName);
            CheckForCredentials(googleMap,R.drawable.location, array, location, length);
            map.put(BankName, array);
            return;
        } else {
            for (String key : list.keySet()) {

               array = list.get(key);
                if (array.get(0).GroupName.equals(GroupName)) {
                    int drawable = key.equals(BankName) ? R.drawable.location : R.drawable.suitable_location_icon ;
                    CheckForCredentials(googleMap,drawable, array, location, length);
                    map.put(key, array);
                }
            }
        }


    }

    private void setMarker(LatLng position,String title, int drawable, GoogleMap map)
    {
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(title)

                .icon(BitmapDescriptorFactory
                        .fromResource(drawable))
                .draggable(true));
    }

    private void CheckForCredentials(GoogleMap map, int drawable, ArrayList<Atmosphera> array, LatLng location, int length)
    {

        ArrayList<Atmosphera> list = new ArrayList<Atmosphera>();
        for (Atmosphera item : array)
        {
            if (getDistance(item,location, length))
            {
                list.add(item);
                LatLng position = new LatLng(item.Location.Latitude, item.Location.Longitude);

                setMarker(position, item.BankName ,drawable, map );
            }
        }
       array =  list;

    }

    public boolean getDistance(Atmosphera item, LatLng location, int length)
    {
        if (length <= 0 || location == null || item.Location == null) return false;
        Location loc = item.Location;
        double x = loc.Latitude;
        double y = loc.Longitude;

       return Math.sqrt(Math.pow((x-location.latitude),2) + Math.pow((y - location.longitude),2)) <= length;
    }


    public String contains(Set<String> array, String selector)
    {
        for(String item: array)
        {
            if (item.toLowerCase().contains(selector.toLowerCase()) || item.toLowerCase().equals(selector.toLowerCase()))
            {
                return item;
            }
        }
        return "";
    }


    public boolean isEnabled(String mode)
    {
        try {
            return  Boolean.parseBoolean(mode);
        }
        catch (Exception e)
        {
            return false;
        }
    }



    public boolean isMatched(String text, String mode)
    {
        int position = getPositionInArray(array, mode);
        Pattern p =  Pattern.compile("");
     switch(position)
     {
         case 0:{

             p = Pattern.compile("");
             break;
         }
         case 1:{
             p = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)\\b");
             break;
         }
         case 2:{
             p = Pattern.compile("(\\+)?\\d{9,}");
             break;
         }
         case 3: {
             p = Pattern.compile("(\\+)?\\d{9,}");
             break;
         }
         case 4:{
             p = Pattern.compile("\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
             break;
         }

     }

        Matcher m = p.matcher(text);
        if (m.find())
        {
            return true;
        }
        return false;
    }

    public int getPositionInArray(String[] array, String element)
    {
        for(int i = 0; i< array.length; i++)
        {
            if (element.equals(array[i]))
                return i;
        }
        return 0;
    }

    public ArrayList<String> toArrayList(Set<String> list)
    {
        ArrayList<String> array = new ArrayList<String>();
        if (list == null) return  array;

        for (String item :  list)
        {
            array.add(item);
        }

        return array;
    }

    public String ToString(List<TechTypes> techTypes)
    {
        String[] array = new String[techTypes.size()];
        int i = 0;
        for (TechTypes element: techTypes)
        {
            array[i] = element.TechType;
            i++;
        }

        return ArrayToString(array, ", ");
    }

    public String[] ToArray(List<String> list)
    {
        String[] array = new String[list.size()];
        int i = 0;
        for (String element: list)
        {
            array[i] = element;
            i++;
        }

        return array;
    }



    public String[][] ToArray(TagInformation data)
    {
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> properties = new ArrayList<String>();

        if (data.Text!= null)
        {
            values.add(data.Text);
            properties.add("Text");
        }
        values.add(ToString(data.TechTypes));
        properties.add("Tech Types");
        if (data.Type!= null)
        {
            values.add(data.Type);
            properties.add("Type");

        }

        return new String[][]{ToArray(properties), ToArray(values)};
    }
    public String[][] ToArray(TagDetails data)
    {
        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> properties = new ArrayList<String>();

        if (data.Id!= null)
        {
            values.add(data.Id);
            properties.add("Id");
        }
        if (data.ATQA!= null)
        {
            values.add(data.ATQA);
            properties.add("ATQA");
        }
        if (data.SAK!= null)
        {
            values.add(data.SAK);
            properties.add("SAK");
        }
        if (data.HistoricalBytes!= null)
        {
            values.add(data.HistoricalBytes);
            properties.add("HistoricalBytes");
        }

       return new String[][]{ToArray(properties), ToArray(values)};

    }
    public String[][] ToArray(NdefMessageInformation data)
    {

        ArrayList<String> values = new ArrayList<String>();
        ArrayList<String> properties = new ArrayList<String>();

        if (data.Id!= null)
        {
            values.add(data.Id);
            properties.add("Id");
        }
        if (data.Type!= null && data.Type!="")
        {
            values.add(data.Type);
            properties.add("Type");
        }

        if (data.Payload!= null)
        {
            values.add(data.Payload);
            properties.add("Payload");
        }

        return new String[][]{ToArray(properties), ToArray(values)};

    }



    public String ArrayToString(String[] array, String separator)
    {
        if (array == null) return null;
        StringBuilder  arrayString = new StringBuilder();

        for(String item: array)
        {
            arrayString.append(item);
            arrayString.append(separator);
        }
        return arrayString.substring(0,arrayString.length() - separator.length());
    }

    public final static boolean isFeatureAvailable(Context context, String feature) {
        final PackageManager packageManager = context.getPackageManager();
        final FeatureInfo[] featuresList = packageManager.getSystemAvailableFeatures();
        for (FeatureInfo f : featuresList) {
            if (f.name != null && f.name.equals(feature)) {
                return true;
            }
        }

        return false;
    }

    public byte[] hex2Byte(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) Integer
                    .parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String getHex( byte [] raw ) {
        String HEXES = "0123456789ABCDEF";
        if (raw == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }


    public void MessageBox(String title, String text,final  Activity activity, final Activity next, final Activity back, final String data)
    {
        //push some extra data if not a null
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(title)
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent myIntent = new Intent(activity, next.getClass());
                        myIntent.putExtra("result",data);
                        activity.startActivityForResult(myIntent, 1);
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent myIntent = new Intent(activity, back.getClass());
                activity.startActivityForResult(myIntent, 1);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
