package BussinessLogic.Common.BankReaders;

import android.content.Context;

import com.example.myapplication5.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import BussinessLogic.Abstract.IBankReaderTask;
import Models.Atmosphera;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Solomiia on 6/16/2014.
 */
public class AtmospheraBankReaderTask implements IBankReaderTask {

    Context context;
    String city;

    public AtmospheraBankReaderTask(Context context, String city)
    {
        this.context =  context;
        this.city = city;
    }
    @Override
    public ArrayList<Atmosphera> readEntry() {
        return getDataFromAtmosphera();
    }


    private   ArrayList<Atmosphera>  getDataFromAtmosphera()
    {

        ArrayList<Atmosphera> list = new ArrayList<Atmosphera>();

        try {
            InputStream inputWorkbook = context.getResources().openRawResource(R.drawable.atm_apr14);
            Workbook w;
            w = Workbook.getWorkbook(inputWorkbook);

            Sheet sheet = w.getSheet(0);
            int j = 0;

            for (int i = 0; i < sheet.getRows(); i++) {


                String content =sheet.getCell(2,i).getContents();
                if (!content.equals(city))
                {
                    continue;
                }

                Atmosphera model = new Atmosphera();
                switch(j) {
                    case 0: {
                        model.BankName = sheet.getCell(j, i).getContents();
                        j+=2;
                    }
                    case 2: {
                        model.City = sheet.getCell(j, i).getContents();
                        j+=2;
                    }
                    case 4:{
                        model.Street = sheet.getCell(j, i).getContents();
                        j = 0;
                        break;
                    }
                }
                model.GroupName = "Атмосфера";


                list.add(model);



            }

        } catch (BiffException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

      return list;

    }
}
