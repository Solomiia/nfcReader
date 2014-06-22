package BussinessLogic.SqlLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;

import BussinessLogic.Common.DataHelper;

/**
 * Created by Solomiia on 4/24/2014.
 */
public class MySqlLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "NFCDatabase.db";
    private final Context mContext;

    SQLiteDatabase db;

    public MySqlLiteHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TagDetails_TABLE =
                "CREATE TABLE If not exists TagDetails ( " +
                "Identifier INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Id TEXT, "+
                "ATQA TEXT, "+
                "SAK TEXT, "+
                "HistoricalBytes TEXT);";

        db.execSQL(CREATE_TagDetails_TABLE);

        String CREATE_NdefMessageInformation_TABLE =
                "CREATE TABLE If not exists NdefMessageInformation ( " +
                "Identifier INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Type TEXT, "+
                "TextEncoding TEXT, "+
                "LanguageCodeLength INTEGER, "+
                "Payload TEXT);";

        db.execSQL(CREATE_NdefMessageInformation_TABLE);

        String CREATE_TagInformation_TABLE =
                "CREATE TABLE If not exists  TagInformation ( " +
                "Identifier INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ImagePath Text," +
                "Text TEXT, "+
                "TagDetailsId INTEGER REFERENCES TagDetails(Identifier) ON UPDATE CASCADE,"+
                "Type TEXT," +
                "NdefMessageInformationId INTEGER REFERENCES NdefMessageInformation(Identifier) ON UPDATE CASCADE);";

        db.execSQL(CREATE_TagInformation_TABLE);

        String CREATE_TechTypes_TABLE =
                "CREATE TABLE If not exists  TechTypes ( " +
                "Identifier INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TechType TEXT, "+
                "TagInformationId INTEGER REFERENCES TagInformation(Id) ON UPDATE CASCADE);";

        db.execSQL(CREATE_TechTypes_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TagDetails");
        db.execSQL("DROP TABLE IF EXISTS NdefMessageInformation");
        db.execSQL("DROP TABLE IF EXISTS TagInformation");
        db.execSQL("DROP TABLE IF EXISTS TechTypes");
        // create fresh books table
        this.onCreate(db);
    }


    @Override
    public synchronized SQLiteDatabase getWritableDatabase(){

        try {
               db = super.getWritableDatabase();

        }
        catch (SQLiteException e) {
            Log.d("Tag", e.getMessage());
            File dbFile = mContext.getDatabasePath(DATABASE_NAME);
            Log.d("Tag","db path="+dbFile.getAbsolutePath());
            db = SQLiteDatabase.openOrCreateDatabase(dbFile.getAbsolutePath(), null);
        }
        return db;
    }



    @Override
         public synchronized SQLiteDatabase getReadableDatabase(){

        try {
              db = super.getReadableDatabase();

        }
        catch (SQLiteException e) {
            Log.d("Tag", e.getMessage());
            File dbFile = mContext.getDatabasePath(DATABASE_NAME);
            Log.d("Tag","db path="+dbFile.getAbsolutePath());

            db = SQLiteDatabase.openOrCreateDatabase(dbFile.getAbsolutePath(), null);
        }
        return db;
    }
    public void closeDatabase()
    {
        if (db.isOpen())
        db.close();
    }


    public int insertDataTable(String DataTable, ContentValues contentValues)
    {

    SQLiteDatabase db = this.getWritableDatabase();
    int elem = (int) db.insert(DataTable, null, contentValues);
    db.close();
    if (elem <= 0) {
        return 0;
    }

    return elem;
    }


    public void deleteElement(String DataTable, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataTable, "id = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void deleteAll(String DataTable) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataTable, null, null);
        db.close();
    }

    public void updateContact(String DataTable, ContentValues values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

     db.update(DataTable, values, "id = ?",
             new String[]{String.valueOf(id)});
    }
    public Cursor selectElement(String DataTable, String[] columns, int id){

        SQLiteDatabase db = this.getWritableDatabase();

    DataHelper dataHelper = new DataHelper();
    String columnSelector = dataHelper.ArrayToString(columns,", ");


        if (columns == null && id == 0)
        {
            return  db.rawQuery("SELECT * FROM " + DataTable+ ";", null);
        }
        else if(columns == null)
        {
           return db.rawQuery("SELECT * FROM " + DataTable + " WHERE Id = " + id + ";", null);
        }
        else if(id == 0)
        {
            return db.rawQuery("SELECT "+ columnSelector+ " FROM " + DataTable + ";", null);
        }
        else
        {
            return  db.rawQuery("SELECT "+ columnSelector+ " FROM "+ DataTable + " WHERE Id = " + id + ";", null);
        }
    }
    public Cursor selectElement(String DataTable){
       return selectElement(DataTable, null,0);
    }
    public Cursor selectElement(String DataTable, int id){
       return selectElement(DataTable, null,id);
    }
    public Cursor selectElement(String DataTable, String[] columns){
             return selectElement(DataTable, columns,0);
    }
    public int GetDataTableAmount(){

        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteStatement statement = db.compileStatement("SELECT COUNT(*) FROM TagInformation;");
        int sometotal = (int)statement.simpleQueryForLong();

        return sometotal;
    }

    public Cursor selectElement(String DataTable, String WhereCondition)
    {
        SQLiteDatabase db = this.getWritableDatabase();
       Cursor c =  db.rawQuery("SELECT * FROM "+ DataTable + WhereCondition + ";", null);

        return c;
    }


}
