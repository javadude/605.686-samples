package com.javadude.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private SharedPreferences preferences;
	private TextView nameField;
	private EditText nameEntry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameField = (TextView) findViewById(R.id.nameField);
		nameEntry = (EditText) findViewById(R.id.nameEntry);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		String currentName = preferences.getString("some_name2", "No Name");
		nameEntry.setText(currentName);
		nameEntry.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				SharedPreferences.Editor edit = preferences.edit();
				edit.putString("some_name2", s.toString());
				edit.apply();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		String name = preferences.getString("example_text", "Nobody");
		assert nameField != null;
		nameField.setText(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
