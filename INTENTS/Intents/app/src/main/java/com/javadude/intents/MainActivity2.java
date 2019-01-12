package com.javadude.intents;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

// Example of an Activity that can handle a request and return a result
public class MainActivity2 extends AppCompatActivity {
    // the data we read from the incoming intent
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflate our user interface
        setContentView(R.layout.activity_main2);

        // read the data from the incoming intent
        data = getIntent().getStringExtra("data");

        // display the incoming data in our user interface
        TextView textView = (TextView) findViewById(R.id.data);
        textView.setText(data);
    }

    // Handle the "Ok" button
    public void onOkPressed(View view) {
        // create an intent to hold the return data
        Intent dataIntent = new Intent();

        // convert the incoming data to uppercase
        String resultInfo = data.toUpperCase();

        // store the resulting data in the intent
        dataIntent.putExtra("resultInfo", resultInfo);

        // set the result to "ok" and set up to return the data intent
        setResult(RESULT_OK, dataIntent);

        // tell the activity we're done
        // note that this does not _immediately_ quit; it just says
        //   "when we're done with this user thread action, go back"
        finish();

        // CAUTION: if more code here, still gets executed!!!
    }

    // Handle the "Cancel" button
    public void onCancelPressed(View view) {
        // just set the result to cancel; we have no data to return
        //   so we don't need to set a result intent
        setResult(RESULT_CANCELED);

        // tell the activity we're done
        // note that this does not _immediately_ quit; it just says
        //   "when we're done with this user thread action, go back"
        finish();

        // CAUTION: if more code here, still gets executed!!!
    }
}
