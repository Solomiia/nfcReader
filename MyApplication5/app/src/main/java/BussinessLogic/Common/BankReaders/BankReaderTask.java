package BussinessLogic.Common.BankReaders;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import BussinessLogic.Abstract.BankEnum;
import BussinessLogic.Abstract.IBankReaderTask;
import BussinessLogic.Abstract.IExcelTask;
import Models.Atmosphera;

/**
 * Created by Solomiia on 6/16/2014.
 */
public class BankReaderTask extends AsyncTask<String,Void,ArrayList<Atmosphera>> {

    String bankName;
    String city;
    Context context;
    HashMap<BankEnum, IBankReaderTask> banks;
    IExcelTask listener;

    public BankReaderTask(Context context, IExcelTask listener)
    {
      this.context = context;
      this.listener = listener;
    }


    @Override
    protected ArrayList<Atmosphera> doInBackground(String... params) {

       bankName = params[0];
       city =params[1];
       String groupName = params[2];

       banks = new HashMap<BankEnum, IBankReaderTask>();
       banks.put(BankEnum.AtmospheraBank, new AtmospheraBankReaderTask(context,city));
       banks.put(BankEnum.PrivatBank, new PrivatBankReader(context, city, bankName));


        IBankReaderTask bankReaderListener = null;
       if (groupName != null && groupName.equals("Атмосфера"))
       {
           bankReaderListener = banks.get(BankEnum.AtmospheraBank);
       }
       if (groupName == null && bankName!= null && bankName.equals("ПриватБанк"))
       {
           bankReaderListener = banks.get(BankEnum.PrivatBank);
       }
       if (bankReaderListener == null) {
           return null;
       }
       else {
           return bankReaderListener.readEntry();
       }
    }
    @Override
    protected void onPostExecute(ArrayList<Atmosphera> list)
    {

        listener.SetResult(list);

    }
}
