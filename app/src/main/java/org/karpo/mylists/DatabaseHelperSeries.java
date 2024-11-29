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

public class DatabaseHelperSeries extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "series_name";
    private final ArrayList<String> TABLES_TO_CREATE;


    public DatabaseHelperSeries(@Nullable Context context, ArrayList<String> TABLES_TO_CREATE) {
        super(context, "my_series_database", null, 1);
        this.TABLES_TO_CREATE = TABLES_TO_CREATE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // doing a for loop on all the tables that need to be created, ttc = table to create
        for (String ttc : TABLES_TO_CREATE) {
            String createTable = "CREATE TABLE " + MainActivity.replaceCharsInStringDatabaseReady(ttc) + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN1 + " TEXT)";
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

    /**
     * A function to add an item to a certain table in the database, also makes sure not to add an item if it already exists in the table
     * @param series - the item that will be added to the DB
     * @param tableName - the table the item will be added to
     * @return boolean - true if the insert was successful, false if it was unsuccessful
     */
    public boolean addData(String series, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        series = MainActivity.replaceCharsInStringDatabaseReady(series);
        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);

        // with contentValues var you can write stuff into the database
        ContentValues contentValues = new ContentValues();

        // checking for duplicates in the list
        Cursor data = this.getData(tableName);
        ArrayList<String> dbTableData = new ArrayList<>();

        while (data.moveToNext()) {
            if (data.getString(1).equals(series)) {
                return false;
            }
        }

        // putting the item given into COLUMN1
        contentValues.put(COLUMN1, series);

        // Adding the item given into COLUMN1 in the table chosen
        Log.d(TAG, "addData: Adding " + series + " to " + tableName);

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

        // getting data from the DB while ignoring 'The', 'the' and ''' characters when sorting it alphabetically with no letter case sensitivity
        return db.rawQuery("SELECT * FROM " + tableName +
//                " ORDER BY (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', '')) COLLATE NOCASE ASC;", null);
                " ORDER BY (REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE (series_name, 'The ', ''), 'the ', ''), '''', ''), ':', ''), '\n', ''), '1234-~#', '')) COLLATE NOCASE ASC;", null);
        //todo: fix the order of things it shows, numbers should be last, while characters before, the + 0 ASC; code does something
    }

    // todo: doesn't work when you try to delete a series, fix it
    // deleting a specific series from selected table
    public void deleteItem (String tableName, String seriesToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();

        tableName = MainActivity.replaceCharsInStringDatabaseReady(tableName);
        seriesToDelete = MainActivity.replaceCharsInStringDatabaseReady(seriesToDelete);

        db.execSQL("DELETE FROM " + tableName + " WHERE series_name='" + seriesToDelete + "';");
    }
}
