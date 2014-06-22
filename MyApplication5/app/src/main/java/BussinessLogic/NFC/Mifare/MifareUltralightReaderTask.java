package BussinessLogic.NFC.Mifare;

import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Solomiia on 5/26/2014.
 */
public class MifareUltralightReaderTask extends AsyncTask<Tag,Void,String> {

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];
        return readTag(tag);
    }

    public String readTag(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e("TAG", "IOException while writing MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                }
                catch (IOException e) {
                    Log.e("TAG", "Error closing tag...", e);
                }
            }
        }
        return null;
    }
}
