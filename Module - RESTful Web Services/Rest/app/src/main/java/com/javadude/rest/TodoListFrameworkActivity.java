package com.javadude.rest;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

// New version of the TodoListActivity that has a Toolbar
public class TodoListFrameworkActivity extends FragmentFrameworkActivity<TodoListFrameworkActivity.State, TodoListFrameworkActivity.Event, Long>
implements TodoListFragment.OnTodoListFragmentListener,
	EditFragment.OnEditFragmentListener {

	public enum State implements FragmentFrameworkActivity.State {
		List, Edit, Exit;
	}
	public enum Event implements FragmentFrameworkActivity.Event {
		ItemSelected, Done, Cancel, NewItem, Back;
	}

	private TodoListFragment todoListFragment;
	private EditFragment editFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_framework);

		// NEW: Setup the Toolbar - find it and use it like an ActionBar
		//      We could alternatively directly inflate a menu into it and
		//      set a listener to handle actions. If you only have a single
		//      toolbar and it should be at the top of the activity, it's
		//      simpler to use the standard ActionBar support
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		todoListFragment = (TodoListFragment) getSupportFragmentManager().findFragmentById(R.id.todoListFragment);
		editFragment = (EditFragment) getSupportFragmentManager().findFragmentById(R.id.editFragment);

		stateMachine()
				.fragmentContainer(R.id.fragmentContainer1)
				.fragmentContainer(R.id.fragmentContainer2)

				.stateType(State.class)
				.initialState(State.List)
				.exitState(State.Exit)
				.backEvent(Event.Back)

				.state(State.List)
					.fragmentPriority(R.id.todoListFragment, R.id.editFragment)
					.on(Event.ItemSelected).goTo(State.Edit)
					.on(Event.Done).goTo(State.List)
					.on(Event.Cancel).goTo(State.List)
					.on(Event.NewItem).goTo(State.Edit)
					.on(Event.Back).goTo(State.Exit)

				.state(State.Edit)
					.fragmentPriority(R.id.editFragment, R.id.todoListFragment)
					.on(Event.ItemSelected).goTo(State.Edit)
					.on(Event.Done).goTo(State.List)
					.on(Event.Cancel).goTo(State.List)
					.on(Event.NewItem).goTo(State.Edit)
					.on(Event.Back).goTo(State.List)

				.state(State.Exit);
	}

	@Override
	protected void onStateChanged(State state, Long id) {
		if (id == null)
			id = -1L;
		editFragment.setTodoId(id);
	}

	@Override
	public void onTodoListFragmentItemSelected(long id) {
		handleEvent(Event.ItemSelected, id);
	}

	@Override
	public void onTodoListFragmentCreateItem() {
		handleEvent(Event.ItemSelected, -1L);
	}

	@Override
	public void onEditFragmentDone(long id) {
		handleEvent(Event.Done, id);
	}

	@Override
	public void onEditFragmentCancel(long id) {
		// do nothing!
		handleEvent(Event.Cancel, id);
	}
}
