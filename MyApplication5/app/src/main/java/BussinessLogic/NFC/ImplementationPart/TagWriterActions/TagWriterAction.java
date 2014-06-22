package BussinessLogic.NFC.ImplementationPart.TagWriterActions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Solomiia on 5/13/2014.
 */
public class TagWriterAction  extends AsyncTask<Intent, Boolean, String> {

    Context context;
    SettingsAction _settingsAction;
    Intent intent;

    public TagWriterAction(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Intent... params) {

        this.intent =  params[0];

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String tech = tag.getTechList()[0];
        SharedPreferences settings = context.getSharedPreferences("Activity", 0);
        String mode = settings.getString("SettingsModel","");
        write(mode, tag);
        return mode;

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


    private NdefRecord createRecord(String text) {

        try {
            String lang = "en";
             byte[] textBytes = text.getBytes();
            byte[] langBytes = lang.getBytes("US-ASCII");
            int langLength = langBytes.length;
            int textLength = textBytes.length;

            byte[] payload = new byte[1 + langLength + textLength];
            payload[0] = (byte) langLength;

            System.arraycopy(langBytes, 0, payload, 1, langLength);
            System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

            NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);


            return recordNFC;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void write(String text, Tag tag) {

        try {

                NdefRecord[] records = new NdefRecord[]{createRecord(text)};
                NdefMessage message = new NdefMessage(records);

                checkIfDataFormattable(tag,message);

                Ndef ndef = Ndef.get(tag);
                ndef.connect();

                ndef.writeNdefMessage(message);
                ndef.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }


    private void checkIfDataFormattable(Tag tag, NdefMessage msg)
    {
        NdefFormatable formatable = NdefFormatable.get(tag);
        if (formatable != null)
        {
            try{
                formatable.connect();
                formatable.format(msg);
                formatable.close();
            }catch(Exception e){

            }

        }

    }
}
