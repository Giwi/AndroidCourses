package cesi.com.tchatapp.database.messages;

import android.provider.BaseColumns;

/**
 * The type Messages db.
 */
public class MessagesDB {

    /**
     * sql request to delete entries
     */
    public static final String SQL_DELETE_ENTRIES = "DELETE FROM " + MessageEntry.TABLE_NAME;

    private static final String TEXT_TYPE = " TEXT";

    private static final String LONG_TYPE = " LONG";

    private static final String BOOL_TYPE = " INT";

    private static final String COMMA_SEP = ",";

    /**
     * sql request to create Users table
     */
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
            MessageEntry.COLUMN_MESSAGE + TEXT_TYPE + COMMA_SEP +                //column 2
            MessageEntry.COLUMN_DATE + LONG_TYPE + COMMA_SEP +              //column 3
            MessageEntry.COLUMN_USER+ LONG_TYPE + COMMA_SEP +              //column 4
            "PRIMARY KEY ("+MessageEntry.COLUMN_DATE+", "+MessageEntry.COLUMN_USER+")"+
            " )";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MessagesDB() {
    }

    /**
     * The type Message entry.
     */
/* Inner class that defines the table contents */
    abstract static class MessageEntry implements BaseColumns {

        static final String TABLE_NAME = "messages";

        static final String COLUMN_MESSAGE = "message";

        static final String COLUMN_DATE = "date";

        static final String COLUMN_USER = "user";
    }
}
