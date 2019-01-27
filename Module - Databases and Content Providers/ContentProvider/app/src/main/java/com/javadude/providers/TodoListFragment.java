package com.javadude.providers;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scott on 4/17/2016.
 */
public class TodoListFragment extends Fragment {
	private static final int TODO_LOADER = 42;

	// NEW: used to generate unique IDs within this run
	//      note that this is a bad idea in general; we should
	//      use a database (and will in a later module)
	private long nextId = 1000;

	// our model for the RecyclerView
	private TodoCursorAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
		// get the recycler view
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		// create some dummy data
		Util.updateTodo(getContext(), new TodoItem(-1, "wash car", "make it shine", 2));
		Util.updateTodo(getContext(), new TodoItem(-1, "wash cat", "make it purr", 3));
		Util.updateTodo(getContext(), new TodoItem(-1, "wash cab", "make it go get better fares", 3));
		Util.updateTodo(getContext(), new TodoItem(-1, "wash cake", "you won't want to eat it after", 5));

		// wrap the data in our adapter to use as a model for the recycler view
		adapter = new TodoCursorAdapter(getActivity(), getActivity().getLayoutInflater());
		recyclerView.setAdapter(adapter);

		// layout the items in the recycler view as a vertical list
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		// listen to the adapter to find out when an item has been selected
		adapter.setTodoListListener(new TodoCursorAdapter.TodoListListener() {
			@Override public void itemSelected(long id) {
				if (onTodoListFragmentListener != null)
					onTodoListFragmentListener.onTodoListFragmentItemSelected(id);
			}});

		// set up support for drag/swipe gestures
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
				new ItemTouchHelper.Callback() {
					// specify which drags/swipes we want to support
					@Override
					public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
						int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
						return makeMovementFlags(0, swipeFlags);
					}

					// if an item is being dragged, tell the adapter
					@Override
					public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
						return true;
					}

					// if an item is being swiped, tell the adapter
					@Override
					public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
						Util.delete(getContext(), viewHolder.getItemId());
					}
				}
		);

		// attach the swipe/gesture support to the recycler view
		itemTouchHelper.attachToRecyclerView(recyclerView);

		// NEW: Set up the Floating Action Button to act as "add new item"
		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				TodoItem todoItem = new TodoItem(nextId++, "", "", 1);
				if (onTodoListFragmentListener != null)
					onTodoListFragmentListener.onTodoListFragmentCreateItem();
			}
		});

		return view;
	}

	private OnTodoListFragmentListener onTodoListFragmentListener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (!(context instanceof OnTodoListFragmentListener))
			throw new IllegalStateException("Activities using TodoListFragment must implement TodoListFragment.OnTodoListFragmentListener");
		onTodoListFragmentListener = (OnTodoListFragmentListener) context;
		getActivity().getSupportLoaderManager().initLoader(TODO_LOADER, null, loaderCallbacks);
	}

	@Override
	public void onDetach() {
		onTodoListFragmentListener = null;
		super.onDetach();
		getActivity().getSupportLoaderManager().destroyLoader(TODO_LOADER);
	}

	private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = {
				TodoContentProvider.COLUMN_ID,
				TodoContentProvider.COLUMN_NAME,
				TodoContentProvider.COLUMN_PRIORITY
			};

			return new CursorLoader(
					getActivity(), TodoContentProvider.CONTENT_URI, projection, null, null,
					TodoContentProvider.COLUMN_NAME + " ASC"
			);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (adapter != null)
				adapter.changeCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			if (adapter != null)
				adapter.changeCursor(null);
		}
	};

	public interface OnTodoListFragmentListener {
		void onTodoListFragmentItemSelected(long id);
		void onTodoListFragmentCreateItem();
	}
}
