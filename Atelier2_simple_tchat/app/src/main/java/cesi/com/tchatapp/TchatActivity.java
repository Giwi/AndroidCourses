package cesi.com.tchatapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cesi.com.tchatapp.adapter.MessagesAdapter;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * The type Tchat activity.
 */
public class TchatActivity extends ActionBarActivity {

    ListView listView;
    MessagesAdapter adapter;
    EditText msg;
    String token;
    String userId;
    //private SwipeRefreshLayout swipeRefreshLayout;


    /**
     * On create.
     *
     * @param savedInstance the saved instance
     */
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_tchat);
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        userId = this.getIntent().getExtras().getString(Constants.INTENT_USER_ID);
        if (token == null) {
            Toast.makeText(this, this.getString(R.string.error_no_token), Toast.LENGTH_SHORT).show();
            finish();
        }
        //swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        listView = (ListView) findViewById(R.id.tchat_list);
        msg = (EditText) findViewById(R.id.tchat_msg);
        findViewById(R.id.tchat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    msg.setText("Please add a message");
                    return;
                }
                new SendMessageAsyncTask().execute(msg.getText().toString());
                msg.setText("");
            }
        });


        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);*/
    }

    private void refresh() {
        new GetMessagesAsyncTask(this).execute();
        //  swipeRefreshLayout.setRefreshing(true);
    }


    /**
     * On create options menu boolean.
     *
     * @param menu the menu
     * @return the boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tchat, menu);
        return true;
    }


    /**
     * On options item selected boolean.
     *
     * @param item the item
     * @return the boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tchat_refresh:
                new GetMessagesAsyncTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The type Send message async task.
     */
    protected class SendMessageAsyncTask extends AsyncTask<String, Void, Integer> {

        /**
         * Do in background integer.
         *
         * @param params the params
         * @return the integer
         */
        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            try {
                URL url = new URL(TchatActivity.this.getString(R.string.url_msg));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //set authorization header
                conn.setRequestProperty("X-secure-Token", token);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                JSONObject message = new JSONObject()
                        .put("user_id", userId)
                        .put("content", params[0]);
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                // Send post request
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(message.toString());
                wr.flush();
                wr.close();
                return conn.getResponseCode();
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
         * @param status the status
         */
        @Override
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(TchatActivity.this, TchatActivity.this.getString(R.string.error_send_msg), Toast.LENGTH_SHORT).show();
            } else {
                //DO nothing
            }
        }
    }

    /**
     * The type Get messages async task.
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Message>> {

        Context context;

        /**
         * Instantiates a new Get messages async task.
         *
         * @param context the context
         */
        GetMessagesAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background list.
         *
         * @param params the params
         * @return the list
         */
        @Override
        protected List<Message> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }

            InputStream inputStream = null;

            try {
                URL url = new URL(context.getString(R.string.url_msg));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                //set authorization header
                conn.setRequestProperty("X-secure-Token", token);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                int response = conn.getResponseCode();
                Log.d("TchatActivity", "The response code is: " + response);

                inputStream = conn.getInputStream();
                String contentAsString;
                if (response == 200) {
                    List<Message> listOfMessages = new ArrayList<>();
                    // Convert the InputStream into a string
                    contentAsString = NetworkHelper.readIt(inputStream);
                    JSONArray mess = new JSONArray(contentAsString);
                    for (int i = 0; i < mess.length(); i++) {
                        JSONObject m = mess.getJSONObject(i);
                        Log.i("message", m.toString());
                        listOfMessages.add(new Message(m.getJSONObject("author").getString("username"), m.getString("content"), m.getLong("date")));
                    }
                    return listOfMessages;
                }
                return null;
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
         * @param msgs the msgs
         */
        @Override
        public void onPostExecute(final List<Message> msgs) {
            int nb = 0;
            if (msgs != null) {
                nb = msgs.size();
            }
            Toast.makeText(TchatActivity.this, "loaded nb messages: " + nb, Toast.LENGTH_LONG).show();
            adapter.addMessage(msgs);
        }
    }
}
