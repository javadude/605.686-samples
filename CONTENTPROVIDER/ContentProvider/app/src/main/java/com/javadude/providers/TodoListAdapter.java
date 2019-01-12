package com.javadude.providers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

// an adapter for a RecyclerView to manage TodoItem objects
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {
	// the real list of TodoItems that we're adapting
	private List<TodoItem> items;

	// the activity's layout inflater, needed to create instances of the row views
	private LayoutInflater layoutInflater;

	public TodoListAdapter(LayoutInflater layoutInflater, List<TodoItem> items) {
		this.layoutInflater = layoutInflater;
		this.items = items;
	}

// if we want to have different views for different rows, override getItemViewType
//   and return a number representing the row. The entire set of numbers should be
//   contiguous starting with 0
//	@Override
//	public int getItemViewType(int position) {
//		if (position % 2 == 0)
//			return 0;
//		return 1;
//	}

	// create a ViewHolder that contains a view of the specified type
	@Override
	public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = layoutInflater.inflate(R.layout.item, parent, false);
		return new TodoViewHolder(view);
	}

	// fill the data into the view for the specified row
	@Override
	public void onBindViewHolder(TodoViewHolder holder, int position) {
		final TodoItem todoItem = items.get(position);
//		holder.view.setBackgroundColor(position % 2 == 0 ? Color.LTGRAY : Color.WHITE);
		holder.name.setText(todoItem.getName());
		holder.priority.setText(String.valueOf(todoItem.getPriority()));

		// listen to the overall view for clicks - if clicked, notify
		//   the listener so it can navigate
		holder.view.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				todoListListener.itemSelected(todoItem);
			}});
	}

	// indicate how many items the recycler view will hold
	@Override
	public int getItemCount() {
		return items.size();
	}

	// react to an item being dragged
	public void onItemMoved(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		// find out which positions the view holders are in
		int fromPosition = viewHolder.getAdapterPosition();
		int toPosition = target.getAdapterPosition();

		// from the "from" view up or down, swapping with all views between it and its destination
		if (fromPosition < toPosition) {
			for(int i = fromPosition; i < toPosition; i++) {
				Collections.swap(items, i, i+1);
			}
		} else {
			for(int i = fromPosition; i > toPosition; i--) {
				Collections.swap(items, i, i-1);
			}
		}

		// tell any listeners (in this case, the RecyclerView) that the item has been moved
		notifyItemMoved(fromPosition, toPosition);
	}

	// react to an item being swiped
	public void onItemDismissed(RecyclerView.ViewHolder viewHolder) {
		// find out where the view that was swiped is
		int position = viewHolder.getAdapterPosition();

		// remove the item in that position from the actual list
		items.remove(position);

		// tell any listeners (in this case, the RecyclerView) that the item has been removed
		notifyItemRemoved(position);
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

	// lookup a to-do item by name and replace it in the list
	// NEW: changed this method to lookup items by their id
	//      (would be better to use a LinkedHashMap to store the items
	//      by their ids - the LinkedHashMap keeps the order of insertion.
	//      This would have required more changes so I skipped that approach.
	//      Better still... we really should use a database, which is in a later
	//      module in the course)
	public void update(TodoItem todoItem) {
		boolean found = false;
		for(int i = 0; i < items.size(); i++) {
			TodoItem item = items.get(i);
			if (item.getId() == todoItem.getId()) {
				items.set(i, todoItem);
				found = true;
				notifyItemChanged(i);
				break;
			}
		}
		if (!found) {
			items.add(todoItem);
			notifyItemInserted(items.size()-1);
		}
	}

	// define a listener interface that we can call to indicate that an item has been clicked
	private TodoListListener todoListListener;

	public void setTodoListListener(TodoListListener todoListListener) {
		this.todoListListener = todoListListener;
	}

	public interface TodoListListener {
		void itemSelected(TodoItem todoItem);
	}
}
