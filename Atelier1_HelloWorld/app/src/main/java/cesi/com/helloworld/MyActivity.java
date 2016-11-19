package cesi.com.helloworld;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cesi.com.helloworld.helper.NetworkHelper;

/**
 * The type My activity.
 */
public class MyActivity extends Activity{

    TextView textView;
    EditText editText;
    Button button;
    ProgressDialog progressDialog;

    /**
     * On create.
     *
     * @param savedInstanceBundle the saved instance bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.activity_hello);
        textView = (TextView)findViewById(R.id.texview);
        editText = (EditText)findViewById(R.id.edittext);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do my request
                displayProgressDialog();
                new HelloAsyncTask(v.getContext()).execute(editText.getText().toString());
            }
        });


    }

    /**
     * display a Progress Dialog.
     */
    private void displayProgressDialog() {
        progressDialog =  new ProgressDialog(MyActivity.this);
        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("hello in progress ...");
        progressDialog.show();
    }

    /**
     * Method to close progress dialog.
     */
    private void hideProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        } else {
            Log.w("HelloWorld", "trying to close Progress dialog that is not exist or opened");
        }
    }

    /**
     * The type Hello async task.
     */
    public class HelloAsyncTask extends AsyncTask<String, Void, String>{

        Context context;

        /**
         * Instantiates a new Hello async task.
         *
         * @param context the context
         */
        HelloAsyncTask(final Context context){
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
            if(!NetworkHelper.isInternetAvailable(context)){
                return "Internet not available";
            }
            return NetworkHelper.connect(params[0]);
        }

        /**
         * On post execute.
         *
         * @param s the s
         */
        @Override
        protected void onPostExecute(final String s) {
            hideProgressDialog();
            textView.setText(s);
        }
    }
}
