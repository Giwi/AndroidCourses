package cesi.com.helloworld.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The type Network helper.
 */
public class NetworkHelper {
    private static String SERVICE_URL = "http://vps288382.ovh.net/api/1";


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
     * Connect string.
     *
     * @param name the name
     * @return the string
     */
    public static String connect(String name) {
        // Un stream pour récevoir la réponse
        InputStream inputStream = null;

        try {
            URL url = new URL(SERVICE_URL + "/hello/" + name);
            Log.d("Calling URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("NetworkHelper", "The response code is: " + response);
            inputStream = conn.getInputStream();
            // Convert the InputStream into a string
            return NetworkHelper.readIt(inputStream);
            // Makes sure that the InputStream is closed after the app is finished using it.
        } catch (Exception e) {
            Log.e("NetworkHelper", e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("NetworkHelper", e.getMessage());
                }
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private static String readIt(InputStream stream) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while ((ch = stream.read()) != -1) {
            sb.append((char) ch);
        }
        Reader reader = new InputStreamReader(stream, "UTF-8");
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        return sb.toString();
    }
}
