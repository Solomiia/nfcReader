package BussinessLogic.NFC.ImplementationPart;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.NFC.ImplementationPart.TagWriterActions.TagWriterAction;

/**
 * Created by Solomiia on 5/13/2014.
 */
public class NFCWriter extends AsyncTask<Intent, Boolean, String> {


    private TagWriterAction _tagWriterAction;

    private Context context;
    private IAsyncTask _asyncTaskListener;

    public NFCWriter (IAsyncTask asyncTaskListener)
    {
        this.context = (Context)asyncTaskListener;
        _asyncTaskListener = asyncTaskListener;
    }



    @Override
    protected String doInBackground(Intent... params) {
        Intent intent = params[0];
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        _tagWriterAction = new TagWriterAction(context);
        try {


            String tech = tag.getTechList()[0];
            String ndefTech = Ndef.class.getName();
            String isoTech = IsoDep.class.getName();
            String mifare = MifareUltralight.class.getName();
            Log.d("NFCTechList", "Reading Data");

                if (isoTech.equals(tech)) {
                    return Boolean.toString(false);

                }
                if (ndefTech.equals(tech) || mifare.equals(tech)) {
                    String data = String.valueOf(_tagWriterAction.execute(intent));
                    return Boolean.toString(data != null);
                }


        }
        catch (Exception e)
        {

        }
        return null;

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
