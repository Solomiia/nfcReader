package BussinessLogic.Common;

import android.content.Context;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Models.Atmosphera;
import Models.Location;

/**
 * Created by Solomiia on 5/19/2014.
 */
public class ExcelReader {

    Context context;
    private String fileName = "file.xls";

    public ExcelReader(Context context)
    {
        this.context = context;
    }

    static Map<String,ArrayList<Atmosphera>> map = new HashMap<String, ArrayList<Atmosphera>>();

    private ArrayList<Atmosphera> read(String city){

        ArrayList<Atmosphera> list = new ArrayList<Atmosphera>();

        FileInputStream fIn = null;
        HSSFWorkbook w;
        try {
            fIn =  context.openFileInput(fileName);
            POIFSFileSystem fileSystem = new POIFSFileSystem(fIn );
            w = new HSSFWorkbook(fileSystem);

            HSSFSheet sheet = w.getSheet("Sheet1");
            int j = 0;

            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {

                String content =sheet.getRow(i).getCell(1).getStringCellValue();
                if (!content.equals(city))
                {
                    continue;
                }
                Atmosphera model = new Atmosphera();
                model.Location = new Location();
                switch(j) {
                    case 0: {
                        model.BankName = sheet.getRow(i).getCell(j).getStringCellValue();
                        j++;
                    }
                    case 1: {
                        model.City = sheet.getRow(i).getCell(j).getStringCellValue();
                        j++;
                    }
                    case 2: {
                        model.Street = sheet.getRow(i).getCell(j).getStringCellValue();
                        j++;
                    }
                    case 3: {
                        model.Location.Latitude = Double.parseDouble(sheet.getRow(i).getCell(j).getStringCellValue());
                        j++;
                    }
                    case 4:{
                        model.Location.Longitude = Double.parseDouble(sheet.getRow(i).getCell(j).getStringCellValue());
                        j = 0;
                    }
                    case 5: {
                        model.GroupName = (sheet.getRow(i).getCell(j).getStringCellValue() == null) ? "" : sheet.getRow(i).getCell(j).getStringCellValue();
                        j++;
                        break;
                    }
                }


                list.add(model);



            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String,ArrayList<Atmosphera>> readFromExcel(String city){


        try {
            ArrayList<Atmosphera> list = read(city);
            ArrayList<Atmosphera> tempList = new ArrayList<Atmosphera>();
            Collections.sort(list, new Atmosphera.Comparators().BankName);

            if (list.size() == 0) return map;
            String key = list.get(0).BankName;
            for (Atmosphera item : list) {
                if (item.BankName.equals(key)) {
                    tempList.add(item);
                } else {
                    map.put(key, tempList);
                    key = item.BankName;
                    tempList = new ArrayList<Atmosphera>();
                    tempList.add(item);
                }
            }

            return map;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return  map;
    }


}
