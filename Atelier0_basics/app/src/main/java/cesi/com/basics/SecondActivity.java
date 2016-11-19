package cesi.com.basics;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * The type Second activity.
 */
public class SecondActivity extends Activity {


    /**
     * On create.
     *
     * @param savedInstance the saved instance
     */
    @Override
    public void onCreate(final Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.secondactivity);

        //get intent data
        String text = this.getIntent().getStringExtra("VALUE");
        ((TextView)findViewById(R.id.textview)).setText(text);
    }
}
