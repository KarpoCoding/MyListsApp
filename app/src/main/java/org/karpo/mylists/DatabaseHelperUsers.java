package org.karpo.mylists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class DatabaseHelperUsers extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "username";
    private static final String COLUMN2 = "password";
    private final String USERS_TABLE = "usernames_and_passwords";

    public DatabaseHelperUsers(@Nullable Context context) {
        super(context, "my_users_database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + USERS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN1 + " TEXT, " + COLUMN2 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     *
     * @param username - the name of the user
     * @param password - the user's password
     * @return boolean according to if the user insert was successful or not
     */
    public boolean addNewUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // making the item name ready for DB
        String hashed_password = hashString(password);

        // with contentValues var you can write stuff into the database
        ContentValues contentValues = new ContentValues();

        // checking for duplicates in the list
        Cursor data = this.getAllUsersData();
        ArrayList<String> dbTableData = new ArrayList<>();

        // if the user already exists return False
        while (data.moveToNext()) {
            if (data.getString(1).equals(username)) {
                return false;
            }
        }

        // putting the item given into COLUMN1, COLUMN2
        contentValues.put(COLUMN1, username);
        contentValues.put(COLUMN2, hashed_password);

        // Adding the item given into COLUMN1 in the table chosen
        Log.d(TAG, "addData: Adding " + username + " to " + USERS_TABLE);

        // putting inside the 'result' var the info about if it was added successfully
        long result = db.insert(USERS_TABLE, null, contentValues);

        // if inserted incorrectly it will return -1, aka false, otherwise it will return true
        return result != -1;
    }

    /**
     * getting the data from the database alphabetically.
     * @return the table data alphabetically - an Arraylist of Strings.
     */
    public Cursor getAllUsersData() {
        SQLiteDatabase db = this.getWritableDatabase();

        // getting all of the users from the DB
        return db.rawQuery("SELECT * FROM " + USERS_TABLE + ";", null);
    }

    public Cursor getUserInfo(String user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // getting the user info from the db
        return db.rawQuery("SELECT * FROM " + USERS_TABLE + " WHERE username = '" + user + "';", null);
    }

    /**
     * checks if the user exists in the users db
     * @param username - the username
     * @return true if the user does exist, else false
     */
    public boolean checkIfUserExists (String username) {
        Cursor data = getAllUsersData();

        while (data.moveToNext()) {
            if (data.getString(1).equals(username)) {
                return true;
            }
        }
        return false;
    }

//    /**
//     * deleting all data from selected table
//     */
//    public void deleteAllData() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + USERS_TABLE);
//    }

//    /**
//     * deleting a specific user from the users table
//     * @param userToDelete - the user it will delete
//     */
//    public void deleteUser (String userToDelete) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + USERS_TABLE + " WHERE username = '" + userToDelete + "';");
//    }

    /**
     * updating the user's password
     * @param user - the user
     * @param newPassword - new password
     */
    public void updateUserPassword(String user, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        String hashedNewPassword = hashString(newPassword);

        db.execSQL("UPDATE " + USERS_TABLE + " SET password = '" + hashedNewPassword + "' WHERE username = '" + user + "';");
    }

    /**
     * @param stringToHash - the string it will hash
     * @return a hashed string
     */
    public String hashString(String stringToHash){
    // code taken from Stack Overflow
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(stringToHash.getBytes(Charset.forName("US-ASCII")),0,stringToHash.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}

