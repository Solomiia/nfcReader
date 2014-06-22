package BussinessLogic.SqlLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import BussinessLogic.Common.DataHelper;
import Models.NdefMessageInformation;
import Models.TagDetails;
import Models.TagInformation;
import Models.TechTypes;


public class SqlImplementation{


    private MySqlLiteHelper _mySqlLiteHelper;
    private DataHelper _dataHelper;
    private NdefMessageInformation _ndefMessageInformation;
    private TagDetails _tagDetails;
    private TagInformation _tagInformation;
    private TechTypes _techTypes;


    public SqlImplementation(Context context){

        _dataHelper = new DataHelper();
        _mySqlLiteHelper = new MySqlLiteHelper(context);
        _ndefMessageInformation = new NdefMessageInformation();
        _tagDetails = new TagDetails();
        _tagInformation = new TagInformation();
        _techTypes = new TechTypes();


    }

    public MySqlLiteHelper returnSqlBaseInstance()
        {
            return _mySqlLiteHelper;
        }


    private int InsertIntoNdefMessageInformationTable(NdefMessageInformation data)
    {
        if (data == null) return 0;
        ContentValues values = new ContentValues();
        values.put("'Type'",data.Type);
        values.put("'TextEncoding'",data.TextEncoding);
        values.put("'LanguageCodeLength'",data.LanguageCodeLength);
        values.put("'Payload'",data.Payload);

       return  _mySqlLiteHelper.insertDataTable("NdefMessageInformation", values);
    }

    private void InsertIntoTechTypesTable(TagInformation information)
    {
        //todo: check if tag information has id
        if (information.TechTypes == null) return;
        List<TechTypes> data = information.TechTypes;
        ContentValues values = new ContentValues();
        for(TechTypes techType: information.TechTypes)
        {
            values.put("'TechType'",techType.TechType);
            values.put("'TagInformationId'", information.Identifier);
        }

         _mySqlLiteHelper.insertDataTable("TechTypes", values);
    }

    public int InsertIntoDataTableValues(TagInformation information)
    {
        try {
            if (information.ndefMessageInformation!= null) {
                information.ndefMessageInformation.Identifier = InsertIntoNdefMessageInformationTable(information.ndefMessageInformation);
            }
            if (information.TagDetails!= null)
            {
            information.TagDetails.Identifier = InsertIntoTagDetailsTable(information.TagDetails);
            }

                information.Identifier = InsertIntoTagInformationTable(information);
            InsertIntoTechTypesTable(information);
        }
        catch (Exception e)
        {
            Log.d("Exception", e.getMessage());
        }
        return  information.Identifier;
    }

    private int InsertIntoTagInformationTable(TagInformation data) {
        if (data == null) return 0;
        ContentValues values = new ContentValues();
        values.put("'ImagePath'",data.ImagePath);
        values.put("'Text'",data.Text);
        if (data.TagDetails == null)
        {
            values.put("'TagDetailsId'", "NULL");
        }else {
            values.put("'TagDetailsId'", data.TagDetails.Identifier);
        }
        values.put("'Type'",data.Type);

        if (data.ndefMessageInformation == null)
        {
            values.put("'ndefMessageInformationId'","NULL");
        }else {
            values.put("'ndefMessageInformationId'",data.ndefMessageInformation.Identifier);
        }

       return _mySqlLiteHelper.insertDataTable("TagInformation", values);

    }

    private int InsertIntoTagDetailsTable(TagDetails data) {
        if (data == null) return 0;
        ContentValues values = new ContentValues();
        values.put("'Id'",data.Id);
        values.put("'ATQA'",data.ATQA);
        values.put("'SAK'",data.SAK);
        values.put("'HistoricalBytes'",data.HistoricalBytes);

      return  _mySqlLiteHelper.insertDataTable("TagDetails", values);

    }

    public Map<Integer,String> getImagePathes(){
        //ImagePath Identifier
       String[] columns = new String[]{"Identifier","ImagePath"};

       Cursor cursor = _mySqlLiteHelper.selectElement("TagInformation", columns);
        HashMap<Integer, String> map = new HashMap<Integer, String>();

        int i = 0;
        if (cursor.moveToFirst()){
            while(cursor.isAfterLast()==false)
            {
                i++;
                int key = cursor.getInt(0);
                String value = cursor.getString(1);

                map.put(key,value);
                cursor.moveToNext();
            }
        }
        cursor.close();
        _mySqlLiteHelper.closeDatabase();
       return map ;
    }



    public boolean isTableNotEmpty(){
        if (_mySqlLiteHelper.GetDataTableAmount() > 0) {
            return true;
        }
        return false;
    }

    public TagInformation getTagInformation(int index)
    {
        TagInformation tagInformation = new TagInformation();
        tagInformation.TagDetails = new TagDetails();
        tagInformation.ndefMessageInformation =  new NdefMessageInformation();
        tagInformation.TechTypes = new ArrayList<TechTypes>();


        String whereCondition = " WHERE " +"Identifier"+" = '" + index + "'";

        Cursor cursor = _mySqlLiteHelper.selectElement("TagInformation", whereCondition);


        String name = "";
        if (cursor.moveToFirst()){
            while(cursor.isAfterLast()==false)
            {
                    tagInformation.Identifier = cursor.getInt(0);
                    tagInformation.ImagePath = cursor.getString(1);
                    tagInformation.Text = cursor.getString(2);
                    tagInformation.Type = cursor.getString(4);

                    int temp = cursor.getInt(3);

                    tagInformation.TagDetails = cursor.isNull(3) ? null : GetDetailsData(cursor.getInt(3));
                    if (tagInformation.TagDetails != null && tagInformation.TagDetails.Identifier == 0)
                    {
                        tagInformation.TagDetails = null;
                    }

                    tagInformation.ndefMessageInformation = cursor.isNull(5) ? null: GetData(cursor.getInt(5));
                if (tagInformation.ndefMessageInformation != null && tagInformation.ndefMessageInformation.Identifier == 0)
                {
                    tagInformation.ndefMessageInformation = null;
                }

                    cursor.moveToNext();
                    cursor.close();


            }
        }

        tagInformation.TechTypes = GetTechData(tagInformation.Identifier);

        _mySqlLiteHelper.closeDatabase();
        return tagInformation;
    }

    private NdefMessageInformation GetData(int index)
    {
        NdefMessageInformation ndefMessageInformation = new NdefMessageInformation();
        String whereCondition = " WHERE " +"Identifier"+" = '" + index + "'";
        Cursor cursor = _mySqlLiteHelper.selectElement("NdefMessageInformation", whereCondition);

      //  Cursor cursor = _mySqlLiteHelper.selectElement("NdefMessageInformation");
try {
    if (cursor.moveToFirst()) {
        while (cursor.isAfterLast() == false) {
            if (cursor.getInt(0) == index) {
                ndefMessageInformation.Identifier = cursor.getInt(0);
                ndefMessageInformation.Type = cursor.getString(1);
                ndefMessageInformation.TextEncoding = cursor.getString(2);
                ndefMessageInformation.LanguageCodeLength = cursor.getInt(3);
                ndefMessageInformation.Payload = cursor.getString(4);
            }
            else
            {
                Log.d("Identifier",cursor.getString(0));
            }
            cursor.moveToNext();
        }

    }
}catch(Exception e)
{
    return null;
}
   cursor.close();
        return ndefMessageInformation;
    }

    private List<TechTypes> GetTechData(int index)
    {

        TechTypes techTypes = new TechTypes();
        List<TechTypes> list = new ArrayList<TechTypes>();
        String whereCondition = " WHERE "+ "TagInformationId" + "='"+ index + "'";
     try {
         Cursor cursor = _mySqlLiteHelper.selectElement("TechTypes", whereCondition);


         if (cursor.moveToFirst()) {
             while (cursor.isAfterLast() == false) {

                 techTypes.Identifier = cursor.getInt(0);
                 techTypes.TechType = cursor.getString(1);

                 cursor.moveToNext();

                 list.add(techTypes);


             }
         }
         cursor.close();

     }catch (Exception e){
         return null;
     }
        return list;
    }

    private TagDetails GetDetailsData(int index)
    {
        TagDetails tagDetails = new TagDetails();
        String whereCondition = " WHERE "+"Identifier"+"='" + index + "'";
        Cursor cursor = _mySqlLiteHelper.selectElement("TagDetails", whereCondition);
       // Cursor cursor = _mySqlLiteHelper.selectElement("TagDetails");

try {
    if (cursor.moveToFirst()) {
        while (cursor.isAfterLast() == false) {
            if (cursor.getInt(0) == index) {

                tagDetails.Identifier = cursor.getInt(0);
                tagDetails.Id = cursor.getString(1);
                tagDetails.ATQA = cursor.getString(2);
                tagDetails.SAK = cursor.getString(3);
                tagDetails.HistoricalBytes = cursor.getString(4);

            }
            cursor.moveToNext();
        }

    }
}
catch(Exception e){
        return null;
    }

        cursor.close();
        return tagDetails;
    }

}
