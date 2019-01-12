package com.javadude.nav2;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.javadude.nav2.R;
public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	public void gotoAction(View view) {
		startActivity(new Intent(this, ActionActivity.class));
	}
	public void gotoHorror(View view) {
		startActivity(new Intent(this, HorrorActivity.class));
	}
	public void gotoScifi(View view) {
		startActivity(new Intent(this, ScifiActivity.class));
	}
}
