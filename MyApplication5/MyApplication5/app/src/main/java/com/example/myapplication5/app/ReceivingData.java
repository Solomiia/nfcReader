package com.example.myapplication5.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.NFC.ImplementationPart.NFCReader;
import BussinessLogic.NFC.ImplementationPart.NFCWriter;
import BussinessLogic.NFC.ImplementationPart.TagWriterActions.SettingsAction;
import Models.SettingsModel;


public class ReceivingData extends Activity implements IAsyncTask {

    private NFCReader _nfcReader;
    private NfcAdapter mNfcAdapter;
    private DataHelper _dataHelper;
    private MainActivity _mainActivity;
    private TakePicture _takePicture;
    private SettingsAction _settingsAction;
    private BroadcastReceiver receiver;
    private NFCWriter _nfcWriter;
    private String mode;
    SharedPreferences settings;
    TextView textView;


    public ReceivingData(){
     _nfcReader = new NFCReader(this);
     _dataHelper = new DataHelper();
     _mainActivity = new MainActivity();
     _takePicture = new TakePicture();
     _nfcWriter = new NFCWriter(this);


 }



@Override
protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.nfc_data_page);

    mode = getIntent().getStringExtra("Activity");
    settings = getSharedPreferences("Activity", 0);
    String settingsModel = getIntent().getStringExtra("SettingsModel");
    if (mode!= null)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Mode", mode);
        editor.putString("SettingsModel", settingsModel);
        editor.commit();

    }



    mNfcAdapter = _nfcReader.CreateAdapter(this);

    if (mNfcAdapter == null)
    {
        Log.d("CreateAdapter", "is null");
        return;
    }

    final Intent intent = getIntent();
    onNewIntent(intent);
}

    public void onPause(){
        try{
            super.onPause();
            mNfcAdapter.disableForegroundDispatch(this);
        } catch(Exception e)
        {
            Log.d("OnPause", e.toString());

        }
    }

    public void onResume(){
        try{
            super.onResume();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
            {
                onNewIntent(getIntent());
            }
        }catch(Exception e)
        {
           Log.d("OnResume",e.toString());

        }
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            mode = settings.getString("Mode","");
            if (mode.equals("Writer")) {
                _nfcWriter.execute(intent);


            }
            else {
                _nfcReader.execute(intent);
            }
        }
    }


    @Override
    public void getResult(String data) {
        if (data!= null)
        {
            if (mode.equals("Writer"))
            {
                GoToWriteMode(data);
              //  settings.edit().remove("Mode").commit();
            }
            else{
                GoToReadMode(data);
            }
        }



    }



    private void GoToWriteMode(String result)
    {
        if (result == null) return;
        boolean isEnabled = _dataHelper.isEnabled(result);
        if (isEnabled)
        {
            _dataHelper.MessageBox("Success", "Return to main menu?",this, _mainActivity,this, null);
        }
        else {
            _dataHelper.MessageBox("Type Ndef is not supported", "Will you select another card?",this,this, _mainActivity, getIntent().getStringExtra("SettingsModel"));
        }
    }

    private void GoToReadMode(String data){
        try{

            SettingsModel model  = new Gson().fromJson(data, SettingsModel.class);
            if (model == null)
            {
                throw new Exception();
            }
            Intent intent = getIntent();
            intent.putExtra("SettingsModel", data);
            _settingsAction = new SettingsAction(this,intent);
            _settingsAction.execute();

        }catch(Exception e)
        {
            _dataHelper.MessageBox("SaveData","Do you want to save data", this, _takePicture, _mainActivity, data);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }
}

