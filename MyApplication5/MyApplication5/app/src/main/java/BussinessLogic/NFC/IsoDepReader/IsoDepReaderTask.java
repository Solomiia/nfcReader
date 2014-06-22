package BussinessLogic.NFC.IsoDepReader;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import BussinessLogic.Abstract.IAsyncTask;
import BussinessLogic.Common.DataHelper;
import Models.TagDetails;
import Models.TagInformation;
import Models.TechTypes;

/**
 * Created by Solomiia on 4/9/2014.
 */
public class IsoDepReaderTask extends AsyncTask<Tag, Void, String> {

    private IsoDep isodep;
    private DataHelper _dataHelper;
    private ArrayList<Byte> data;
    private byte[] hash;
    private IAsyncTask _asyncTaskListener;
    public static final String EXTRA_SAK = "sak";
    public static final String EXTRA_ATQA = "atqa";
    private short mSak;
    private byte[] mAtqa;
    private byte[] id;

    public  IsoDepReaderTask(IAsyncTask asyncTaskListener){
        _dataHelper = new DataHelper();
        _asyncTaskListener = asyncTaskListener;
    }



    @Override
    protected String doInBackground(Tag... tags) {
        Tag tag = tags[0];
        isodep = IsoDep.get(tag);
        NfcA nfcA = NfcA.get(tag);

        byte[] getHistoricalBytes = isodep.getHistoricalBytes(); //only for NFCA

            mSak = nfcA.getSak();
            mAtqa = nfcA.getAtqa();
            id = nfcA.getTag().getId();


        TagInformation information = new TagInformation();
        information.TechTypes = new ArrayList<TechTypes>();

        TechTypes techTypes = new TechTypes();
        for (String techItem: tag.getTechList())
        {
            techTypes.TechType = techItem;
            information.TechTypes.add(techTypes);

        }
        TagDetails _tagDetails = new TagDetails();
        _tagDetails.HistoricalBytes =  _dataHelper.getHex(getHistoricalBytes);
        _tagDetails.SAK = Integer.toHexString(mSak);
        _tagDetails.ATQA = _dataHelper.getHex(mAtqa);
        _tagDetails.Id = _dataHelper.getHex(id);

        information.TagDetails =_tagDetails;
        information.ndefMessageInformation = null;


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
