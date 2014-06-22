package BussinessLogic.NFC.NdefReader;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import BussinessLogic.Abstract.IAsyncTask;
import Models.NdefMessageInformation;
import Models.TagDetails;
import Models.TagInformation;
import Models.TechTypes;

public class NdefReaderTask extends AsyncTask<Tag, Void, String> {


    private IAsyncTask _asyncTaskListener;

    public NdefReaderTask(IAsyncTask asyncTaskListener){
        _asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {

                try {
                    TagInformation information = new TagInformation();
                    information.TechTypes = new ArrayList<TechTypes>();

                    TechTypes techTypes = new TechTypes();
                    for (String techItem: tag.getTechList())
                    {
                        techTypes.TechType = techItem;
                        information.TechTypes.add(techTypes);

                    }
                    TagDetails _tagDetails = null;
                    information.TagDetails =_tagDetails;

                    return readText(ndefRecord,information);
                } catch (UnsupportedEncodingException e) {
                    Log.e("NFC", "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record, TagInformation information) throws UnsupportedEncodingException {

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        NdefMessageInformation ndefMessageInformation = new NdefMessageInformation();
        ndefMessageInformation.Id = new String(record.getId(),textEncoding);
        ndefMessageInformation.Type =new String(record.getType(), textEncoding);
        ndefMessageInformation.TextEncoding = textEncoding;
        ndefMessageInformation.LanguageCodeLength = languageCodeLength;
        ndefMessageInformation.Payload = new String(payload, textEncoding);

        information.ndefMessageInformation = ndefMessageInformation;
        information.Text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

        String json = new Gson().toJson(information);

        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("NFC", "Read content: " + result);
            HasResult = true;
            _asyncTaskListener.getResult(result);
        }
    }
    public boolean HasResult;
}
