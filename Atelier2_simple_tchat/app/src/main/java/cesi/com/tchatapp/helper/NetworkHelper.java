package cesi.com.tchatapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * The type Network helper.
 */
public class NetworkHelper {


    /**
     * Is internet available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("HelloWorld", "Error on checking internet:", e);

        }
        //default allowed to access internet
        return true;
    }

    /**
     * Read it string.
     *
     * @param stream the stream
     * @return the string
     * @throws IOException the io exception
     */
// Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
