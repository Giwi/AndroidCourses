package cesi.com.tchatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.utils.Constants;

/**
 * The type Users activity.
 */
public class UsersActivity extends Activity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    String token = null;
    private SwipeRefreshLayout swipeLayout;

    /**
     * On create.
     *
     * @param savedInstace the saved instace
     */
    @Override
    public void onCreate(Bundle savedInstace) {
        super.onCreate(savedInstace);
        setContentView(R.layout.activity_users);
        token = getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new LinkedList<String>());
        list.setAdapter(adapter);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.users_swiperefresh);
        setupRefreshLayout();
    }


    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        loading();
    }

    private void loading() {
        swipeLayout.setRefreshing(true);
        new GetUsersAsyncTask().execute();
    }

    /**
     * Setup refresh layout
     */
    private void setupRefreshLayout() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading();
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        /**
         * this is to avoid error on double scroll on listview/swipeRefreshLayout
         */
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (list != null && list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeLayout.setEnabled(enable);
            }
        });
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetUsersAsyncTask extends AsyncTask<String, Void, List<String>> {

        /**
         * Do in background list.
         *
         * @param params the params
         * @return the list
         */
        @Override
        protected List<String> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(UsersActivity.this)) {
                return null;
            }

            InputStream inputStream = null;

            try {
                URL url = new URL(UsersActivity.this.getString(R.string.url_users));
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
                    List<String> listOfUsers = new ArrayList<>();
                    // Convert the InputStream into a string
                    contentAsString = NetworkHelper.readIt(inputStream);
                    JSONArray users = new JSONArray(contentAsString);
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject u = users.getJSONObject(i);
                        listOfUsers.add(u.getString("username"));
                    }
                    return listOfUsers;
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
         * @param users the users
         */
        @Override
        public void onPostExecute(final List<String> users) {
            if (users != null) {
                adapter.clear();
                adapter.addAll(users);
            }

            adapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
        }
    }
}
