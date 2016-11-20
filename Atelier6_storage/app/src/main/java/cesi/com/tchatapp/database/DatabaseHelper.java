package cesi.com.tchatapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cesi.com.tchatapp.database.messages.MessagesDB;

/**
 * The type Database helper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "tchatApp.db";

    /**
     * Instantiates a new Database helper.
     *
     * @param context the context
     */
    public DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * On create.
     *
     * @param db the db
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(MessagesDB.SQL_CREATE_ENTRIES);
    }

    /**
     * On upgrade.
     *
     * @param sqLiteDatabase the sq lite database
     * @param i              the
     * @param i2             the 2
     */
    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i2) {
        //DO NOTHING for now
    }
}