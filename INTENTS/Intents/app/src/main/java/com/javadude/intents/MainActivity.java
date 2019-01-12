package com.javadude.intents;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

// An activity that acts as a hub to send intents to other activities
public class MainActivity extends AppCompatActivity {
    // For use when callinh startActivityForResult
    // This is a number used only inside this activity to differentiate
    //   between different types of startActivityForResult calls
    //   (maybe we want to request a barcode be scanned and an image be edited...
    //    having distinct values let us identify what results are coming back
    //    in onActivityResult)
    private static final int DATA_REQUEST = 42;

    // This field is used to enter text to be sent to be modified in MainActivity2
    private EditText editText;

    // This view displays the resulting modified text returned from MainActivity2
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate our user interface
        setContentView(R.layout.activity_main);

        // locate the fields we'll read and update
        editText = (EditText) findViewById(R.id.data);
        resultText = (TextView) findViewById(R.id.resultText);
    }

    // Handle Button 1 (explicit intent example)
    public void onButton1Pressed(View view) {
        // read the data from the field
        String data = editText.getText().toString();

        // create an intent to start MainActivity2
        Intent intent = new Intent(this, MainActivity2.class);

        // attach the data entered by the user to the intent
        //   so MainActivity2 can read it
        intent.putExtra("data", data);

        // start the activity, asking for a result
        // This requires that we override onActivityResult to see the response
        startActivityForResult(intent, DATA_REQUEST);
    }

    // Handle Button 2 (implicit intent example)
    public void onButton2Pressed(View view) {
        // create an implicit intent to "view" something
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // specify the type of data to view
        // (normally we'd also attach some data so we know what to view)
        intent.setType("application/vnd.javadude.data");

        // start the activity
        startActivity(intent);
    }

    // Handle Button 3 (Web Browse example)
    public void onButton3Pressed(View view) {
        // create an implicit intent to "view" something
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // indicate that we're viewing HTML, and add the URL from the field
        String data = editText.getText().toString();
        intent.setDataAndType(Uri.parse(data), "text/html");

        // start the activity
        startActivity(intent);
    }

    // Handle a result coming back from a startActivityForResult request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check which request this is a response for
        if (requestCode == DATA_REQUEST) {
            // was the result "ok" from the called activity?
            if (resultCode == RESULT_OK) {
                // get the result info and put it in the text view
                String resultInfo = data.getStringExtra("resultInfo");
                resultText.setText(resultInfo);
            }
            return;
        }
        // let the superclass do its work for other requests (if it does anything)
        super.onActivityResult(requestCode, resultCode, data);
    }
}
