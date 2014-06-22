package BussinessLogic.Common.BankReaders;

import android.content.Context;

import java.util.ArrayList;

import BussinessLogic.Abstract.IBankReaderTask;
import BussinessLogic.Common.XMLParser.HttpRequestClass;
import BussinessLogic.Common.XMLParser.XmlBankParser;
import Models.Atmosphera;

/**
 * Created by Solomiia on 6/16/2014.
 */
public class PrivatBankReader implements IBankReaderTask {

    HttpRequestClass httpRequestClass;
    XmlBankParser xmlBankParser;

    Context context;
    String city;
    String bank;

    public PrivatBankReader(Context context, String city, String bank)
    {
        this.context = context;
        this.city = city;
        this.bank = bank;
        this.httpRequestClass = new HttpRequestClass(context, city);
        this.xmlBankParser = new XmlBankParser(bank, city);
    }

    @Override
    public ArrayList<Atmosphera> readEntry() {
        String response = httpRequestClass.SendHttpGetRequest(bank);
        return xmlBankParser.readEntry(response);

    }
}
