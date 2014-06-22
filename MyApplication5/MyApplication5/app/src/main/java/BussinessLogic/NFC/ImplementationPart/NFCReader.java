package BussinessLogic.NFC.ImplementationPart;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import BussinessLogic.NFC.IsoDepReader.IsoDepReaderTask;
import BussinessLogic.NFC.NdefReader.NdefReaderTask;

public class NFCReader extends AsyncTask<Intent, Boolean, String>{


    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[]  intentFiltersArray;
    private String[][] techListsArray;

    private DataHelper _dataHelper;
    private IsoDepReaderTask _isoDepReaderTask;
    private NdefReaderTask _ndefReaderTask;

    private IAsyncTask _asyncTaskListener;


    public NFCReader (IAsyncTask asyncTaskListener){
        _dataHelper = new DataHelper();
        _isoDepReaderTask = new IsoDepReaderTask(asyncTaskListener);
        _ndefReaderTask = new NdefReaderTask(asyncTaskListener);
        _asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected String doInBackground(Intent... intents) {
        Intent intent = intents[0];
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            Log.d("onNewIntent", "type is a " + type);
            if ("text/plain".equals(type)) {

                return String.valueOf(_ndefReaderTask.execute(tag));

            } else {
                Log.d("NFC", "Wrong mime type: " + type);
                return null;
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            String[] techList = tag.getTechList();
            String ndefTech = Ndef.class.getName();
            String isoTech = IsoDep.class.getName();
            Log.d("NFCTechList","Reading Data");
            for (String tech : techList) {
                if (ndefTech.equals(tech)) {
                   return String.valueOf(_ndefReaderTask.execute(tag));
                }
                if (isoTech.equals(tech))
                {
                    return String.valueOf(_isoDepReaderTask.execute(tag));

                }
            }
        }
        Log.d("NFC", "NoItemsFound");
        return null;
    }

    public NfcAdapter CreateAdapter(Activity activity)
    {
        if (!_dataHelper.isFeatureAvailable(activity, PackageManager.FEATURE_NFC)) {
            Toast.makeText(activity, "NFC not supported", Toast.LENGTH_LONG).show();
        } else {
            NfcManager manager = (NfcManager) activity.getSystemService(Context.NFC_SERVICE);
            mNfcAdapter =  manager.getDefaultAdapter();
            if (mNfcAdapter == null)
            {
                Log.d("CreateAdapter", "is null");
                return null;
            }
            Log.d("CreateAdapter","Nfc is supported");

            CreateNFCIntent(activity);

        }
        return mNfcAdapter;
    }

    public void CreateNFCIntent(Activity activity){
        pendingIntent = PendingIntent.getActivity(activity, 0,
                new Intent(activity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            intentFiltersArray = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.d("TagDispatch", e.toString());
        }

        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };

    }

    @Override
    protected void onPostExecute(String result) {
        System.gc();
        if (result != null) {
            Log.d("NFC", "Read content: " + result);
            _asyncTaskListener.getResult(result);
        }
    }
}
