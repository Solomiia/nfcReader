package com.example.myapplication5.app;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

import BussinessLogic.Abstract.ParsedNdefRecord;

/**
 * Created by Solomiia on 4/7/14.
 */
public class NdefMessageParser {

    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static void parse(NdefMessage message) {
        getRecords(message.getRecords());
    }

    public static void getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
        for (final NdefRecord record : records) {
          //  if (UriRecord.isUri(record)) {
          //      elements.add(UriRecord.parse(record));
          //  } else if (TextRecord.isText(record)) {
          //      elements.add(TextRecord.parse(record));
          // } else if (SmartPoster.isPoster(record)) {
          //      elements.add(SmartPoster.parse(record));
          //  } else {
           //     elements.add(new ParsedNdefRecord() {
                  //  @Override
                  //  public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
                     //   TextView text = (TextView) inflater.inflate(R.layout.tag_text, parent, false);
                     //   text.setText(new String(record.getPayload()));
                     //   return text;
                   // }

               // });
         //   }
        }
     //   return elements;
    }
}