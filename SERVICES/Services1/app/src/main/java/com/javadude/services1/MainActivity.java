package com.javadude.services1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, StartedService.class);
		startService(intent);
	}

	@Override
	protected void onStop() {
		Intent intent = new Intent(this, StartedService.class);
		stopService(intent);
		super.onStop();
	}
}
