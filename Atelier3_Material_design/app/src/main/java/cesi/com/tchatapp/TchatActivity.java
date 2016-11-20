package cesi.com.tchatapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cesi.com.tchatapp.adapter.MessagesAdapter;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * The type Tchat activity.
 */
public class TchatActivity extends ActionBarActivity {

    RecyclerView listView;
    FloatingActionButton fab;
    MessagesAdapter adapter;

    String token;
    String userId;

    List<Message> messages;

    private LinearLayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    Timer timer;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refresh();
        }
    };

    private void refresh() {
        new GetMessagesAsyncTask(this).execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        //start polling
        timer = new Timer();
        // first start in 500 ms, then update every TIME_POLLING
        try {
            timer.schedule(task, 500, 5000);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Tchat timertask error", e);
        }
    }

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
        listView = (RecyclerView) findViewById(R.id.tchat_list);
        listView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.tchat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteMsgDialog d = WriteMsgDialog.getInstance(token, userId);
                d.show(TchatActivity.this.getFragmentManager(), "write");

            }
        });

        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupNavigationView(navigationView);
        }

        new GetMessagesAsyncTask(this).execute();
    }

    private void setupNavigationView(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.tchat_disconnect) {
                            //TODO  Your turn
                        }
                        if (menuItem.getItemId() == R.id.tchat_users) {

                        } else if (menuItem.getItemId() == R.id.tchat_tchat) {
                        } else {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                });
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
