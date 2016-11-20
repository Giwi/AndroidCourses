package cesi.com.tchatapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cesi.com.tchatapp.helper.NetworkHelper;

/**
 * The type Signup activity.
 */
public class SignupActivity extends Activity {

    EditText username;
    EditText pwd;
    EditText name;
    EditText firstname;
    ProgressBar pg;
    Button btn;

    /**
     * On create.
     *
     * @param savedInstance the saved instance
     */
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signup);
        username = (EditText) findViewById(R.id.signup_username);
        pwd = (EditText) findViewById(R.id.signup_pwd);
        pg = (ProgressBar) findViewById(R.id.signup_pg);
        name = (EditText) findViewById(R.id.signup_name);
        firstname = (EditText) findViewById(R.id.signup_firstname);

        btn = (Button) findViewById(R.id.signup_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                new SignupAsyncTask(v.getContext()).execute(
                        username.getText().toString(),
                        pwd.getText().toString(),
                        name.getText().toString(),
                        firstname.getText().toString());
            }
        });
    }

    private void loading(boolean loading) {
        if(loading){
            pg.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
        } else {
            pg.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SignupAsyncTask extends AsyncTask<String, Void, Integer> {
        Context context;

        /**
         * Instantiates a new Signup async task.
         *
         * @param context the context
         */
        SignupAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background integer.
         *
         * @param params the params
         * @return the integer
         */
        @Override
        protected Integer doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                //error
                return 404;
            }
            InputStream inputStream = null;
            try {
                URL url = new URL(context.getString(R.string.url_signup));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                JSONObject register = new JSONObject()
                        .put("username", params[0])
                        .put("password", params[1])
                        .put("name", params[2])
                        .put("firstname", params[3]);
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                // Send post request
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(register.toString().getBytes("UTF-8"));
                wr.flush();
                wr.close();
                return conn.getResponseCode();
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
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

        /**
         * On post execute.
         *
         * @param response the response
         */
        @Override
        public void onPostExecute(final Integer response){
            loading(false);
            if(response == 200){
                SignupActivity.this.finish();
            } else {
                Toast.makeText(context, context.getString(R.string.error_signup), Toast.LENGTH_LONG).show();
            }
        }
    }
}