package com.javadude.nav2;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.javadude.nav2.R;
import androidx.core.app.NavUtils;
public class Statham1Activity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statham1);
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
	public void gotoStatham2(View view) {
		startActivity(new Intent(this, Statham2Activity.class));
	}
	public void gotoStatham3(View view) {
		startActivity(new Intent(this, Statham3Activity.class));
	}
	public void gotoStatham4(View view) {
		startActivity(new Intent(this, Statham4Activity.class));
	}
}
