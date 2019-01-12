package com.javadude.providers;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TodoCursorAdapter extends CursorRecyclerViewAdapter<TodoCursorAdapter.TodoViewHolder> {
	// the activity's layout inflater, needed to create instances of the row views
	private LayoutInflater layoutInflater;

	public TodoCursorAdapter(Context context,  LayoutInflater layoutInflater) {
		super(context, null);
		this.layoutInflater = layoutInflater;
	}

	// create a ViewHolder that contains a view of the specified type
	@Override
	public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = layoutInflater.inflate(R.layout.item, parent, false);
		return new TodoViewHolder(view);
	}

	// fill the data into the view for the specified row
	@Override
	public void onBindViewHolder(TodoViewHolder holder, Cursor cursor) {
		final TodoItem todoItem = new TodoItem();
		int idCol = cursor.getColumnIndex(TodoContentProvider.COLUMN_ID);
		int nameCol = cursor.getColumnIndex(TodoContentProvider.COLUMN_NAME);
		int priorityCol = cursor.getColumnIndex(TodoContentProvider.COLUMN_PRIORITY);

		todoItem.setId(cursor.getLong(idCol));
		todoItem.setName(cursor.getString(nameCol));
		todoItem.setPriority(cursor.getInt(priorityCol));

		holder.name.setText(todoItem.getName());
		holder.priority.setText(String.valueOf(todoItem.getPriority()));

		// listen to the overall view for clicks - if clicked, notify
		//   the listener so it can navigate
		holder.view.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				todoListListener.itemSelected(todoItem.getId());
			}});
	}

	// a data object that represents a currently-visible row in the recycler view
	// typically this is used to look up the locations of subviews _once_ and hold
	//   onto those view so
	public static class TodoViewHolder extends RecyclerView.ViewHolder {
		private TextView name;
		private TextView priority;
		private View view;
		public TodoViewHolder(View view) {
			super(view);
			this.view = view;
			name = (TextView) view.findViewById(R.id.name);
			priority = (TextView) view.findViewById(R.id.priority);
		}
	}

	// define a listener interface that we can call to indicate that an item has been clicked
	private TodoListListener todoListListener;

	public void setTodoListListener(TodoListListener todoListListener) {
		this.todoListListener = todoListListener;
	}

	public interface TodoListListener {
		void itemSelected(long id);
	}

}
