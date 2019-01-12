package com.javadude.nav2;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.javadude.nav2.R;
import android.support.v4.app.NavUtils;
public class ScifiActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scifi);
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
	public void gotoStar1(View view) {
		startActivity(new Intent(this, Star1Activity.class));
	}
	public void gotoStar2(View view) {
		startActivity(new Intent(this, Star2Activity.class));
	}
	public void gotoStar3(View view) {
		startActivity(new Intent(this, Star3Activity.class));
	}
	public void gotoAction(View view) {
		startActivity(new Intent(this, ActionActivity.class));
	}
	public void gotoHorror(View view) {
		startActivity(new Intent(this, HorrorActivity.class));
	}
}
