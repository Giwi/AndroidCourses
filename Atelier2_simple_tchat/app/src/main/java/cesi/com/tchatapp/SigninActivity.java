package cesi.com.tchatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.utils.Constants;

/**
 * The type Signin activity.
 */
public class SigninActivity extends Activity {

    EditText username;
    EditText pwd;
    ProgressBar pg;
    Button btn;
    View v;

    /**
     * On create.
     *
     * @param savedInstance the saved instance
     */
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signin);
        v = findViewById(R.id.layout);
        username = (EditText) findViewById(R.id.signin_username);
        pwd = (EditText) findViewById(R.id.signin_pwd);
        pg = (ProgressBar) findViewById(R.id.signin_pg);
        btn = (Button) findViewById(R.id.signin_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                new SigninAsyncTask(v.getContext()).execute(username.getText().toString(), pwd.getText().toString());
            }
        });
        findViewById(R.id.signin_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SignupActivity.class);
                startActivity(i);
            }
        });

    }

    private void loading(boolean loading) {
        if (loading) {
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
    protected class SigninAsyncTask extends AsyncTask<String, Void, String> {
        Context context;
        /**
         * Instantiates a new Signin async task.
         *
         * @param context the context
         */
        SigninAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background string.
         *
         * @param params the params
         * @return the string
         */
        @Override
        protected String doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }
            // Un stream pour récevoir la réponse
            InputStream inputStream = null;
            try {
                URL url = new URL(context.getString(R.string.url_signin));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                JSONObject login = new JSONObject()
                        .put("username", params[0])
                        .put("password", params[1]);
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                // Send post request
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(login.toString());
                wr.flush();
                wr.close();
                int response = conn.getResponseCode();
                Log.d("NetworkHelper", "The response code is: " + response);
                inputStream = conn.getInputStream();
                String contentAsString = null;
                if (response == 200) {
                    // Convert the InputStream into a string
                    contentAsString = NetworkHelper.readIt(inputStream);
                    return contentAsString;
                }
                return contentAsString;
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
         * @param secureToken the secureToken
         */
        @Override
        public void onPostExecute(final String secureToken) {
            loading(false);
            if (secureToken != null) {
                Intent i = new Intent(context, TchatActivity.class);
                try {
                    JSONObject resp = new JSONObject(secureToken);
                    Log.i(Constants.TAG, secureToken);
                    i.putExtra(Constants.INTENT_TOKEN, resp.getString("secureToken"));
                    i.putExtra(Constants.INTENT_USER_ID, resp.getString("user_id"));
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(v, context.getString(R.string.error_login), Snackbar.LENGTH_LONG).setAction("btn", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                    }
                }).show();
            }
        }
    }
}
