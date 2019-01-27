package com.javadude.nav2;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.javadude.nav2.R;
import androidx.core.app.NavUtils;
public class Friday402Activity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friday402);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void gotoFriday403(View view) {
		startActivity(new Intent(this, Friday403Activity.class));
	}
	public void gotoFriday404(View view) {
		startActivity(new Intent(this, Friday404Activity.class));
	}
	public void gotoFriday405(View view) {
		startActivity(new Intent(this, Friday405Activity.class));
	}
}
