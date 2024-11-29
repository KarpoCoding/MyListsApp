package org.karpo.mylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelperLists extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "name";
    private static final String COLUMN2 = "series";
    private static final String COLUMN3 = "series_index";
    private final ArrayList<String> TABLES_TO_CREATE;
//    private final String veryLowNumber = "-999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";


    public DatabaseHelperLists(@Nullable Context context, ArrayList<String> TABLES_TO_CREATE) {
        super(context, "my_lists_database", null, 1);
        this.TABLES_TO_CREATE = TABLES_TO_CREATE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // doing a for loop on all the tables that need to be created, ttc = table to create
        for (String ttc : TABLES_TO_CREATE) {
            String createTable = "CREATE TABLE " + MainActivity.replaceCharsInStringDatabaseReady(ttc) + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN1 + " TEXT, " + COLUMN2 + " TEXT, " + COLUMN3 + " INTEGER)";
            sqLiteDatabase.execSQL(createTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (String ttc : TABLES_TO_CREATE) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ttc);
            onCreate(sqLiteDatabase);
        }
    }

    //todo: add a new function or in a function that already exists functionality that will sort everything alphabetically, perhaps you could implement
    // it in addData function

    /**
     * A function to add an item to a certain table in the database, also makes sure not to add an item if it already exists in the table
     * @param item - the item that will be added to the DB
     * @param tableName - the table the item will be added to
     * @return boolean - true if the insert was successful, false if it was unsuccessful
     */
    public boolean addData(String item, String seriesTheItemBelongsTo, int seriesIndex, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // making the item name ready for DB
        item = MainActivity.replaceCharsInStringDatabaseReady(item);
        seriesTheItemBelongsTo = MainActivity.replaceCharsInStringDatabaseReady(seriesTheItemBelongsTo);

        // with contentValues var you can write stuff into the database
        ContentValues contentValues = new ContentValues();

        // checking for duplicates in the list
        Cursor data = this.getData(tableName);
        ArrayList<String> dbTableData = new ArrayList<>();

        while (data.moveToNext()) {
            if (data.getString(1).equals(item)) {
                return false;
            }
        }

        // putting the item given into COLUMN1, COLUMN2 & COLUMN3
        contentValues.put(COLUMN1, item);
        contentValues.put(COLUMN2, seriesTheItemBelongsTo);
        contentValues.put(COLUMN3, seriesIndex);


        // Adding the item given into COLUMN1 in the table chosen
        Log.d(TAG, "addData: Adding " + item + " to " + tableName);

        // putting inside the 'result' var the info about if it was added successfully
        long result = db.insert(tableName, null, contentValues);

        // if inserted incorrectly it will return -1, aka false, otherwise it will return true
        return result != -1;
    }

    /**
     * getting the data from the database alphabetically.
     * @param tableName is the table from which we'll take the data.
     * @return the table data alphabetically - an Arraylist of Strings.
     */
    public Cursor getData(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);

        // todo: try and write something that will sort the series how it should sort them, if not here then in the getDataFromDB method
        // getting data from the DB while ignoring 'The', 'the' and ''' characters when sorting it alphabetically with no letter case sensitivity
        return db.rawQuery("SELECT * FROM " + tableName +
//                " ORDER BY (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', '')) COLLATE NOCASE ASC;", null);
//                " ORDER BY (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', ''), '1234-~#', '')) COLLATE NOCASE ASC;", null);
//                " ORDER BY IF(series = 'None' OR series = '', (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', ''), '1234-~#', '')) COLLATE NOCASE, series COLLATE NOCASE), series_index;", null);
                // order 'name' ascending when series = 'None' or series = '', else order series ascending, then order by series_index
                " ORDER BY CASE " +
                "WHEN series = 'None' OR series = '' THEN (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', ''), '1234-~#', '')) " +
                "ELSE series END COLLATE NOCASE, series_index;", null);
        //todo: fix the order of things it shows, numbers should be last, while characters before, the + 0 ASC; code does something
        //todo: also fix that the check on 'the ' strings will happen only if they're at the start of the string
    }

    // deleting all data from selected table
    public void deleteAllData (String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);

        db.execSQL("DELETE FROM " + tableName);
    }

    // deleting a specific item from selected table
    public void deleteItem (String tableName, String itemToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);
        itemToDelete = MainActivity.replaceCharsInStringDatabaseReady(itemToDelete);

        db.execSQL("DELETE FROM " + tableName + " WHERE name='" + itemToDelete + "';");
    }

    // applying new value to a specific item from selected table
    public void editItem (String tableName, String itemToEdit, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);
        itemToEdit = MainActivity.replaceCharsInStringDatabaseReady(itemToEdit);
        newValue = MainActivity.replaceCharsInStringDatabaseReady(newValue);

        db.execSQL("UPDATE " + tableName + " SET name = '" + newValue + "' WHERE name = '" + itemToEdit + "';");
    }

    // applying the item's series columns info
    public void editItemsSeriesInfo(String tableName, String itemToEdit, String seriesItBelongsTo, int seriesIndex) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);
        itemToEdit = MainActivity.replaceCharsInStringDatabaseReady(itemToEdit);
        seriesItBelongsTo = MainActivity.replaceCharsInStringDatabaseReady(seriesItBelongsTo);

        db.execSQL("UPDATE " + tableName + " " +
                "SET series = '" + seriesItBelongsTo + "', " +
                "series_index = " + seriesIndex + " " +
                "WHERE name = '" + itemToEdit + "';");
    }
}
