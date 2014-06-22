package BussinessLogic.Common.XMLParser;

import android.content.Context;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Solomiia on 6/13/2014.
 */
public class HttpRequestClass {

    HashMap<String, String> bankRequest;

    Context context;
    String city;

    public HttpRequestClass(Context context, String city)
    {
        //todo: check if there is no changes in PrivatBank request
        this.city  = city;
        this.context = context;
    }

    private void FillInBankUrls()
    {
        if (bankRequest == null) {
            bankRequest = new HashMap<String, String>();
        }

        //todo: check if there is already such request
        bankRequest.put("ПриватБанк", "https://privat24.privatbank.ua/p24/accountorder?oper=prp&atm&address_UA=&city_UA="+ city +"&PUREXML=");


    }

    public String SendHttpGetRequest(String bank)
    {
        if (city== null || city == "" || bank == null || bank =="")
        {
            //todo: return message about bad data
        }

        FillInBankUrls();

        String url = "";
        if (!bankRequest.keySet().contains(bank)) return "";

        url = bankRequest.get(bank);

        String  SetServerString = "";
        HttpClient Client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
          SetServerString = Client.execute(httpget, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SetServerString;
    }
}
