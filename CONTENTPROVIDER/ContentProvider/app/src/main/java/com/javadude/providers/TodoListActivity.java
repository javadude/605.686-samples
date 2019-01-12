package com.javadude.providers;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// New version of the TodoListActivity that has a Toolbar
public class TodoListActivity extends AppCompatActivity
implements TodoListFragment.OnTodoListFragmentListener,
	EditFragment.OnEditFragmentListener {
	// request code for the startActivityForResult call
	private TodoListFragment todoListFragment;
	private EditFragment editFragment;
	private boolean sideBySide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		// NEW: Setup the Toolbar - find it and use it like an ActionBar
		//      We could alternatively directly inflate a menu into it and
		//      set a listener to handle actions. If you only have a single
		//      toolbar and it should be at the top of the activity, it's
		//      simpler to use the standard ActionBar support
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		todoListFragment = (TodoListFragment) getSupportFragmentManager().findFragmentById(R.id.todoListFragment);
		editFragment = (EditFragment) getSupportFragmentManager().findFragmentById(R.id.editFragment);

		sideBySide = (editFragment != null && editFragment.isInLayout());
	}

	@Override
	public void onTodoListFragmentItemSelected(long id) {
		if (sideBySide) {
			editFragment.setTodoId(id);
		} else {
			// if an item is selected, send the item in an intent to the EditActivity
			Intent intent = new Intent(TodoListActivity.this, EditActivity.class);
			intent.putExtra("itemId", id);
			startActivity(intent);
		}
	}

	@Override
	public void onTodoListFragmentCreateItem() {
		if (sideBySide) {
			editFragment.setTodoId(-1);
		} else {
			// create a new dummy item with a unique ID
			// and send it to the edit activity
			Intent intent = new Intent(TodoListActivity.this, EditActivity.class);
			intent.putExtra("itemId", -1L);
			startActivity(intent);
		}
	}

	@Override
	public void onEditFragmentDone(long id) {
		// no longer need to do anything
	}

	@Override
	public void onEditFragmentCancel(long id) {
		// do nothing!
	}
}
