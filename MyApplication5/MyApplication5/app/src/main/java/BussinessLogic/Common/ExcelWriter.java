package BussinessLogic.Common;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import BussinessLogic.Common.BankReaders.BankReaderTask;
import BussinessLogic.GPS.AddressToLocationConverter;
import Models.Atmosphera;
import Models.Location;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by Solomiia on 5/23/2014.
 */
public class ExcelWriter extends BankReaderTask {

    private String fileName = "file.xls";
    private Context context;
    private ArrayList<Location> locationList;
    AddressToLocationConverter converter;
    Geocoder geocoder;

    public ExcelWriter(Context context)
    {
        super(context);
        this.context = context;
    }


    private void createExcelFile() {
        try {

            FileOutputStream  file = null;
            try {
                file = new FileOutputStream(new File(context.getFilesDir(),fileName));
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }


                HSSFWorkbook workbook = new HSSFWorkbook();

                HSSFSheet sheet =  workbook.createSheet("Sheet1");

                HSSFRow rowhead = sheet.createRow(0);

                rowhead.createCell(0).setCellValue("Bank");
                rowhead.createCell(1).setCellValue("City");
                rowhead.createCell(2).setCellValue("Street");
                rowhead.createCell(3).setCellValue("Latitude");
                rowhead.createCell(4).setCellValue("Longitude");




            workbook.write(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean existsData()
    {

        FileInputStream  file = null;
        try {
            file = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            return false;

        }

        try {

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);
            int data = sheet.getPhysicalNumberOfRows();
            if (data > 0)
            {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean existsData(String city)
    {
        FileInputStream  file = null;
        try {
            file = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            return false;

        }

        try {

            HSSFWorkbook workbook = new HSSFWorkbook(file);

            HSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

              if (rows == 0)
            {
                return false;
            }
            for (int i  = 0; i < rows; i++)
            {
                HSSFCell cell  = sheet.getRow(i).getCell(1);
                if (cell.getStringCellValue().equals(city))
                {
                    return true;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean write(ArrayList<Atmosphera> list)
    {
        FileInputStream  file = null;
        FileOutputStream outFile = null;
        File f = new File(context.getFilesDir(), fileName);

        if (!f.exists())
        {
            createExcelFile();
        }
        try {
            file = context.openFileInput(fileName);
            outFile = context.openFileOutput(fileName, context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HSSFWorkbook w;
        try {

            POIFSFileSystem fileSystem = new POIFSFileSystem(file );
            w = new HSSFWorkbook(fileSystem);

            HSSFSheet sheet = w.getSheetAt(0);

            createContent(sheet, list);

            w.write(outFile);


            w = new HSSFWorkbook(file);


            outFile.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return existsData();

    }

    private static boolean rowIsFinded(HSSFSheet sheet, Atmosphera  data) {

        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {

            String city =sheet.getRow(i).getCell(1).getStringCellValue();
            String bank = sheet.getRow(i).getCell(0).getStringCellValue();;
            String address = sheet.getRow(i).getCell(2).getStringCellValue().trim().toLowerCase();
            if (city.equals(data.City) && bank.equals(data.BankName) && address.equals(data.Street.toLowerCase().trim())) {
               return true;
            }
        }
        return false;
    }

    private boolean isRowEmpty(Row row) {

        for (int c = row.getFirstCellNum(); c <= row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;
    }

    private void createContent(HSSFSheet sheetToEdit, ArrayList<Atmosphera> list) throws WriteException {

            System.gc();

            for (Atmosphera item : list) {
                try {
                    if (rowIsFinded(sheetToEdit, item)) {
                        continue;
                    }
                    Location location = getLocation(item.Street);
                    int length =   sheetToEdit.getLastRowNum();
                    Row row = sheetToEdit.getRow(length);
                    int index = length;
                    if (row == null)
                    {
                        sheetToEdit.createRow(length);
                        index = length;
                    }
                    else if (!isRowEmpty(row)) {
                       sheetToEdit.createRow(length+1);
                        index  = length+1;
                    }
                    addToCell(0,index, item.BankName, sheetToEdit);
                    addToCell(1,index, item.City, sheetToEdit);
                    addToCell(2,index, item.Street, sheetToEdit);
                    addToCell(3,index, Double.toString(location.Latitude), sheetToEdit);
                    addToCell(4,index, Double.toString(location.Longitude), sheetToEdit);
                    addToCell(5,index, (item.GroupName == null) ? "" : item.GroupName, sheetToEdit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private Location getLocation(String address)
    {
        if (geocoder == null)
        {
            geocoder = new Geocoder(context, Locale.getDefault());
        }
        Models.Location location = new Models.Location();
        List<Address> locations = null;
        try {
            locations = geocoder.getFromLocationName(address, 1);

        Address loc = locations.get(0);

        location.Latitude = loc.getLatitude();
        location.Longitude = loc.getLongitude();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    private void addToCell(int currentColumn,int index,String value, HSSFSheet sheetToEdit) throws WriteException {

        Cell c = sheetToEdit.getRow(index).getCell(currentColumn);
        if (c == null)
        {
            c = sheetToEdit.getRow(index).createCell(currentColumn);
        }
        c.setCellValue(value);
    }

    //todo: merge banks and writer task


}
