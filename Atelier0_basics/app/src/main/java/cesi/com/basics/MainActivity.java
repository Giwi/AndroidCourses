package cesi.com.basics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The type Main activity.
 */
public class MainActivity extends Activity {

    Button button;
    TextView text;

    /**
     * On create.
     *
     * @param savedInstance the saved instance
     */
    @Override
    public void onCreate(final Bundle savedInstance){
        super.onCreate(savedInstance);
        //set the view for this activity
        setContentView(R.layout.mainactivity);
        //Get the UI from the view
        text = (TextView) findViewById(R.id.my_text);
        button = (Button) findViewById(R.id.my_button);

        //add an action on click button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let's go to other activity
                Intent intent = new Intent(v.getContext(), SecondActivity.class);
                intent.putExtra("VALUE", text.getText());
                startActivity(intent);
            }
        });
    }
}
