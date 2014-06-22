package BussinessLogic.NFC.ImplementationPart.TagWriterActions;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import BussinessLogic.Common.DataHelper;
import Models.MobileSettingsModel;
import Models.SettingsModel;

/**
 * Created by Solomiia on 5/14/2014.
 */
public class SettingsAction {

    Context context;
    Intent intent;

    DataHelper _dataHelper;
    public SettingsAction(Context context, Intent intent)
    {
        this.context = context;
        this.intent = intent;
        _dataHelper = new DataHelper();

    }


    public String[] modes = {Settings.System.AIRPLANE_MODE_ON, Settings.System.ACCELEROMETER_ROTATION};
    public String[] applicationMode = {"android.media.action.IMAGE_CAPTURE"};

    public String[] array =  {"Text","Email", "Message", "Phone number", "Url",  "WIFI", "BLUETOOTH", "Airplane Mode", "Auto Rotation", "Launch Application"};




    public void execute()
    {
        String choice;
        String target;
        String mode;
        String subject;
        String message;
        int position;
        SettingsModel model = new Gson().fromJson(getStringExtra("SettingsModel"), SettingsModel.class);

       if (model.mobileSettingsModels == null)
       {
           choice = model.Choice;
           target = model.Target;
           mode = model.Mode;
           subject = model.Subject;
           message = model.Message;
           position = _dataHelper.getPositionInArray(array,choice);

           executeSelectedMethod(position, target,mode,subject,message);
       }
        else {
           ArrayList<MobileSettingsModel> list = model.mobileSettingsModels;
           for(int i = 0; i< list.size(); i++)
           {
               mode = list.get(i).Mode;
               choice = list.get(i).Choice;
               position = _dataHelper.getPositionInArray(array,choice);
               executeSelectedMethod(position,null,mode,null,null);
           }
       }
    }

    private boolean isEnabled(String mode)
    {
        return _dataHelper.isEnabled(mode);
    }

    private void executeSelectedMethod(int position, String target, String mode, String subject, String message)
    {
        switch(position)
        {
            case 0: {
                try {
                    showText(target);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1: {
                sendEmail(target,subject,message);
                break;
            }
            case 2: {
                sendSMS(target,message);
                break;
            }
            case 3: {
                makeCall(target);
                break;
            }
            case 4: {
                launchURL(target);
                break;
            }
            case 5: {
                turnOnOffWifiMode(isEnabled(mode));
                break;
            }
            case 6: {
                turnOnOffBluetoothMode(isEnabled(mode));
                break;
            }
            case 7: {
                setMode(modes[0],isEnabled(mode));
                break;
            }
            case 8: {
                setMode(modes[1],isEnabled(mode));
                break;
            }
            case 9: {
                //todo make list on Ui
                launchApplication(mode);
                break;
            }

        }
    }



    private String getStringExtra(String header)
    {
        try {
            return intent.getStringExtra(header);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public void setMode(String mode ,boolean isEnabled)
    {
        Settings.System.putInt(
                context.getContentResolver(),
                mode, isEnabled ? 0 : 1);
    }



    public void turnOnOffWifiMode(boolean isEnabled){

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isEnabled);

    }

    public void turnOnOffBluetoothMode(boolean isEnabled) {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean hasBluetooth = (mBluetoothAdapter == null);

        if (hasBluetooth )
        {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON && !isEnabled) {
                mBluetoothAdapter.disable();
            }
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF && isEnabled)
            {
                mBluetoothAdapter.enable();
            }
        }

    }


    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void makeCall(String phoneNumber){

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.getApplicationContext().startActivity(intent);

    }

    public void sendEmail(String emailAddress, String subject, String body)
    {
        sendEmail(emailAddress, subject, body, null);

    }
    public void sendEmail(String emailAddress, String subject, String body, String path){

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        File file = null;
        if (path != null) {
            file = new File(path);
            if (!file.exists() || !file.canRead()) {
                return;
            }
            Uri uri = Uri.fromFile(file);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        else
        {
            emailIntent.setType("message/rfc822");
        }
        context.getApplicationContext().startActivity(Intent.createChooser(emailIntent, "Solomiia's Email provider"));
    }
    public void launchURL(String urlString)
    {
        Uri uri = Uri.parse(urlString);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        context.getApplicationContext().startActivity(launchBrowser);
    }
     public void showText(String data) throws FileNotFoundException {

         String path = context.getFilesDir() + "/text.txt";

         WriteSettings(data, path);
         Scanner input = new Scanner(new File(path));
     }

    public void launchApplication(String mode)
    {

        //camera
        Intent i = new Intent();
        i.setAction(Intent.ACTION_CAMERA_BUTTON);
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CAMERA));
        context.sendOrderedBroadcast(i, null);
    }

    private void WriteSettings(String data, String path)
    {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = context.openFileOutput(path, context.MODE_PRIVATE);
                    osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            Toast.makeText(context, "Settings saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void turnOnOffGPSMode()
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        if(!provider.contains("gps")){ //if gps is disabled
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        }
        else{
           poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        }
        poke.setData(Uri.parse("3"));
        context.sendBroadcast(poke);
    }


}