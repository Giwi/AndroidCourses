package cesi.com.tchatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cesi.com.tchatapp.R;

/**
 * The type Preference helper.
 */
public class PreferenceHelper {

    private static String PREFS = "prefs";

    public static String LOGIN = "login";

    /**
     * Set value.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     */
    public static void setValue(final Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get value string.
     *
     * @param context the context
     * @param key     the key
     * @return the string
     */
    public static String getValue(final Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }
}
