package com.javadude.nav2;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.javadude.nav2.R;
import android.support.v4.app.NavUtils;
public class ActionActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action);
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
	public void gotoStatham1(View view) {
		startActivity(new Intent(this, Statham1Activity.class));
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
	public void gotoHorror(View view) {
		startActivity(new Intent(this, HorrorActivity.class));
	}
	public void gotoScifi(View view) {
		startActivity(new Intent(this, ScifiActivity.class));
	}
}
