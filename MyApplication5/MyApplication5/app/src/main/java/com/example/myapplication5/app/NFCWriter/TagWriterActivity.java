package com.example.myapplication5.app.NFCWriter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication5.app.MainActivity;
import com.example.myapplication5.app.R;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.NFC.ImplementationPart.NFCReader;
import BussinessLogic.NFC.ImplementationPart.NFCWriter;

/**
 * Created by Solomiia on 5/12/2014.
 */
public class TagWriterActivity extends Activity implements IAsyncTask {

    private NFCWriter _nfcWriter;
    private DataHelper _dataHelper;
    private MainActivity _mainActivity;
    private NfcAdapter mNfcAdapter;
    private NFCReader _nfcReader;
    private BroadcastReceiver receiver;

    public TagWriterActivity(){
        _nfcWriter = new NFCWriter(this);
        _dataHelper = new DataHelper();
        _nfcReader = new NFCReader(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_data_page);

        IntentFilter filter = new IntentFilter();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something based on the intent's action
            }
        };
        filter.setPriority(999);
        registerReceiver(receiver, filter);

        _mainActivity  = new MainActivity();
        mNfcAdapter = _nfcReader.CreateAdapter(this);

        if (mNfcAdapter == null)
        {
            Log.d("CreateAdapter", "is null");
            return;
        }
        final Intent intent = getIntent();
        onNewIntent(intent);

    }




    @Override
    protected void onNewIntent(Intent intent){

        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            _nfcWriter.execute(intent);
        }


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
    public void getResult(String result) {

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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
