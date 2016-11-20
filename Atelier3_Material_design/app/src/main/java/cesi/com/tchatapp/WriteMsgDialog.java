package cesi.com.tchatapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The type Write msg dialog.
 */
public class WriteMsgDialog extends DialogFragment {

    private EditText message;

    /**
     * Gets instance.
     *
     * @param token  the token
     * @param userId the user id
     * @return the instance
     */
    static WriteMsgDialog getInstance(final String token, final String userId) {
        WriteMsgDialog f = new WriteMsgDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("user_id", userId);
        f.setArguments(args);

        return f;
    }


    /**
     * On create dialog dialog.
     *
     * @param savedInstanceState the saved instance state
     * @return the dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_msg, null);
        message = (EditText) view.findViewById(R.id.tchat_msg);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        if (!message.getText().toString().isEmpty()) {
                            //post message
                            new SendMessageAsyncTask(view.getContext()).execute(message.getText().toString());
                        } else {
                            message.setError(WriteMsgDialog.this.getActivity()
                                    .getString(R.string.error_missing_msg));
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        WriteMsgDialog.this.dismiss();
                    }
                }

        );

        return builder.create();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SendMessageAsyncTask extends AsyncTask<String, Void, Integer> {

        Context context;

        /**
         * Instantiates a new Send message async task.
         *
         * @param context the context
         */
        SendMessageAsyncTask(final Context context) {
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
            InputStream inputStream = null;

            try {
                URL url = new URL(WriteMsgDialog.this.getString(R.string.url_msg));
                Log.d("Calling URL", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //set authorization header
                conn.setRequestProperty("X-secure-Token", getArguments().getString("token"));
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                JSONObject message = new JSONObject()
                        .put("user_id", getArguments().getString("user_id"))
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
                wr.write(message.toString().getBytes("UTF-8"));
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
                Toast.makeText(context, context.getString(R.string.error_send_msg), Toast.LENGTH_SHORT).show();
            } else {
                WriteMsgDialog.this.dismiss();
            }
        }
    }
}
