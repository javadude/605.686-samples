package com.javadude.providers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by scott on 4/17/2016.
 */
public class EditFragment extends Fragment {
	private EditText name;
	private EditText description;
	private EditText priority;
	private long id; // NEW: hold the id of the todoItem being edited

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null)
			id = savedInstanceState.getLong("id", -1);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_edit, container, false);
		// find the fields for the data
		name = (EditText) view.findViewById(R.id.name);
		description = (EditText) view.findViewById(R.id.description);
		priority = (EditText) view.findViewById(R.id.priority);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("id", id);
	}

	public void setTodoId(long id) {
		this.id = id;
		if (id == -1) {
			name.setText("");
			description.setText("");
			priority.setText("");

		} else {
			TodoItem item = Util.findTodo(getContext(), id);
			if (item == null) {
				name.setText("");
				description.setText("");
				priority.setText("");
				this.id = -1;
			} else {
				name.setText(item.getName());
				description.setText(item.getDescription());
				priority.setText(item.getPriority() + "");
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_edit, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_done:
				saveData();
				if (onEditFragmentListener != null)
					onEditFragmentListener.onEditFragmentDone(id);
				return true;
			case R.id.action_cancel:
				if (onEditFragmentListener != null)
					onEditFragmentListener.onEditFragmentCancel(id);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void saveData() {
		// when the user presses "back", we use that as "save" for now
		//   (we'll replace this with ActionBar buttons later)
		// create a to-do todoItem that we'll return
		TodoItem todoItem = new TodoItem();
		todoItem.setId(id); // NEW: store the id of the todoItem so we can look it up in the list adapter
		todoItem.setName(name.getText().toString());
		todoItem.setDescription(description.getText().toString());
		todoItem.setPriority(Integer.parseInt(priority.getText().toString()));

		Util.updateTodo(getContext(), todoItem);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (!(context instanceof OnEditFragmentListener))
			throw new IllegalStateException("Activities using EditFragment must implement EditFragment.OnEditFragmentListener");
		onEditFragmentListener = (OnEditFragmentListener) context;
	}

	@Override
	public void onDetach() {
		onEditFragmentListener = null;
		super.onDetach();
	}

	private OnEditFragmentListener onEditFragmentListener;

	public interface OnEditFragmentListener {
		void onEditFragmentDone(long id);
		void onEditFragmentCancel(long id);
	}
}
