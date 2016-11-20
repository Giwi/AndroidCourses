package cesi.com.tchatapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cesi.com.tchatapp.R;
import cesi.com.tchatapp.adapter.MessageAdapter;
import cesi.com.tchatapp.database.messages.MessagesDao;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.session.Session;
import cesi.com.tchatapp.utils.Constants;

/**
 * The type Messages fragment.
 */
public class MessagesFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    MessageAdapter adapter;

    /**
     * On create view view.
     *
     * @param inflater           the inflater
     * @param container          the container
     * @param savedInstanceState the saved instance state
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(
                R.layout.fragment_messages, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.messages_list);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.messages_swiperefresh);
        setupRefreshLayout();
        setupRecyclerView();
        return v;
    }

    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        loading(false);
    }

    /**
     * Load messages
     */
    private void loading(boolean force) {
        swipeLayout.setRefreshing(true);
        new GetMessagesAsyncTask(MessagesFragment.this.getActivity()).execute(force);
    }

    /**
     * Setup refresh layout
     */
    private void setupRefreshLayout() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading(true);
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
    }

    /**
     * Setup recycler view.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new MessageAdapter(this.getActivity());
        recyclerView.setAdapter(adapter);

        // Add this. 
        // Two scroller could have problem. 
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetMessagesAsyncTask extends AsyncTask<Boolean, Void, List<Message>> {

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
        protected List<Message> doInBackground(Boolean... params) {
            MessagesDao dao = new MessagesDao(context);
            if(!NetworkHelper.isInternetAvailable(context) || !params[0]){
                return dao.readMessages();
            }
            InputStream inputStream;
            try {
                URL url = new URL(context.getString(R.string.url_msg));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                //set authorization header
                conn.setRequestProperty("X-secure-Token", Session.token);
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
                    dao.writeMessages(listOfMessages);
                    return listOfMessages;
                }
                return null;
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        /**
         * On post execute.
         *
         * @param msgs the msgs
         */
        @Override
        public void onPostExecute(final List<Message> msgs) {
            if (msgs != null) {
                adapter.addMessage(msgs);
            }
            swipeLayout.setRefreshing(false);
        }
    }
}
