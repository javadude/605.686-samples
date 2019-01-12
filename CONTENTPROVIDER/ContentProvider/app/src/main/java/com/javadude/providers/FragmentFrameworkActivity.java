package com.javadude.providers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FragmentFrameworkActivity<
		STATE_TYPE extends Enum<STATE_TYPE> & FragmentFrameworkActivity.State,
		EVENT_TYPE extends FragmentFrameworkActivity.Event,
		EVENT_DATA_TYPE> extends AppCompatActivity {

	public interface Event {
		String name();
	}
	public interface State {
		String name();
	}

	private String currentStateName;
	private STATE_TYPE currentState;
	private STATE_TYPE initialState;
	private STATE_TYPE exitState;
	private EVENT_TYPE backEvent;
	private Class<STATE_TYPE> stateTypeClass;

	private Map<STATE_TYPE, int[]> fragmentPriorities = new HashMap<>();
	private Map<STATE_TYPE, Map<EVENT_TYPE, STATE_TYPE>> transitions = new HashMap<>();

	public interface FragmentContainerSetup<STATE_TYPE, EVENT_TYPE> {
		FragmentContainerOrStateTypeSetup<STATE_TYPE, EVENT_TYPE> fragmentContainer(int id);
	}
	public interface FragmentContainerOrStateTypeSetup<STATE_TYPE, EVENT_TYPE> {
		FragmentContainerOrStateTypeSetup<STATE_TYPE, EVENT_TYPE> fragmentContainer(int id);
		InitialStateSetup<STATE_TYPE, EVENT_TYPE> stateType(Class<STATE_TYPE> stateTypeClass);
	}
	public interface InitialStateSetup<STATE_TYPE, EVENT_TYPE> {
		 ExitStateSetup<STATE_TYPE, EVENT_TYPE> initialState(STATE_TYPE initialState);
	}
	public interface ExitStateSetup<STATE_TYPE, EVENT_TYPE> {
		 BackEventSetup<STATE_TYPE, EVENT_TYPE> exitState(STATE_TYPE exitState);
	}
	public interface BackEventSetup<STATE_TYPE, EVENT_TYPE> {
		 StateSetupSetup<STATE_TYPE, EVENT_TYPE> backEvent(EVENT_TYPE backEvent);
	}
	public interface StateSetupSetup<STATE_TYPE, EVENT_TYPE> {
		 FragmentPrioritiesSetup<STATE_TYPE, EVENT_TYPE> state(STATE_TYPE state);
	}
	public interface FragmentPrioritiesSetup<STATE_TYPE, EVENT_TYPE> {
		 AddEventSetup<STATE_TYPE, EVENT_TYPE> fragmentPriority(int... fragmentPriority);
	}
	public interface AddEventSetup<STATE_TYPE, EVENT_TYPE> {
		AddNextStateSetup<STATE_TYPE, EVENT_TYPE> on(EVENT_TYPE event);
	}
	public interface AddNextStateSetup<STATE_TYPE, EVENT_TYPE> {
		AddEventOrStateSetup<STATE_TYPE, EVENT_TYPE> goTo(STATE_TYPE state);
	}
	public interface AddEventOrStateSetup<STATE_TYPE, EVENT_TYPE> {
		AddNextStateSetup<STATE_TYPE, EVENT_TYPE> on(EVENT_TYPE event);
		FragmentPrioritiesSetup<STATE_TYPE, EVENT_TYPE> state(STATE_TYPE state);
	}
	public class StateMachineBuilder implements
			FragmentContainerSetup<STATE_TYPE, EVENT_TYPE>,
			FragmentContainerOrStateTypeSetup<STATE_TYPE, EVENT_TYPE>,
			InitialStateSetup<STATE_TYPE, EVENT_TYPE>,
			ExitStateSetup<STATE_TYPE, EVENT_TYPE>,
			BackEventSetup<STATE_TYPE, EVENT_TYPE>,
			StateSetupSetup<STATE_TYPE, EVENT_TYPE>,
			FragmentPrioritiesSetup<STATE_TYPE, EVENT_TYPE>,
			AddEventSetup<STATE_TYPE, EVENT_TYPE>,
			AddEventOrStateSetup<STATE_TYPE, EVENT_TYPE>,
			AddNextStateSetup<STATE_TYPE, EVENT_TYPE> {
		private EVENT_TYPE event;
		private Map<EVENT_TYPE, STATE_TYPE> stateTransitions;
		private STATE_TYPE state;

		@Override
		public FragmentContainerOrStateTypeSetup<STATE_TYPE, EVENT_TYPE> fragmentContainer(int id) {
			View fragmentContainer = findViewById(id);
			if (fragmentContainer != null) {
				fragmentContainers.add(fragmentContainer);
			}
			return this;
		}

		@Override
		public InitialStateSetup<STATE_TYPE, EVENT_TYPE> stateType(Class<STATE_TYPE> stateTypeClass) {
			FragmentFrameworkActivity.this.stateTypeClass = stateTypeClass;
			return this;
		}
		@Override
		public ExitStateSetup<STATE_TYPE, EVENT_TYPE> initialState(STATE_TYPE initialState) {
			FragmentFrameworkActivity.this.initialState = initialState;
			return this;
		}
		@Override
		public BackEventSetup<STATE_TYPE, EVENT_TYPE> exitState(STATE_TYPE exitState) {
			FragmentFrameworkActivity.this.exitState = exitState;
			return this;
		}
		@Override
		public StateSetupSetup<STATE_TYPE, EVENT_TYPE> backEvent(EVENT_TYPE backEvent) {
			FragmentFrameworkActivity.this.backEvent = backEvent;
			return this;
		}
		@Override
		public FragmentPrioritiesSetup<STATE_TYPE, EVENT_TYPE> state(STATE_TYPE state) {
			this.state = state;
			stateTransitions = transitions.get(state);
			if (stateTransitions == null) {
				stateTransitions = new HashMap<>();
				transitions.put(state, stateTransitions);
			}
			return this;
		}
		@Override
		public AddEventSetup<STATE_TYPE, EVENT_TYPE> fragmentPriority(int... fragmentPriority) {
			fragmentPriorities.put(state, fragmentPriority);
			return this;
		}
		@Override
		public AddNextStateSetup<STATE_TYPE, EVENT_TYPE> on(EVENT_TYPE event) {
			this.event = event;
			return this;
		}
		@Override
		public AddEventOrStateSetup<STATE_TYPE, EVENT_TYPE> goTo(STATE_TYPE nextState) {
			stateTransitions.put(event, nextState);
			return this;
		}
	}

	protected FragmentContainerSetup<STATE_TYPE, EVENT_TYPE> stateMachine() {
		return new StateMachineBuilder();
	}

	private List<View> fragmentContainers = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			currentStateName = savedInstanceState.getString("currentState");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (transitions.isEmpty())
			throw new IllegalStateException("Must call statemachine() to set up the state machine before onResume()");

		dumpStateMachine();
		if (currentStateName != null) {
			currentState = Enum.valueOf(stateTypeClass, currentStateName);
		} else {
			currentState = initialState;
		}
		showCurrentState();
	}

	private void dumpStateMachine() {
		// using GraphViz dot format
		String dot = "digraph {\n";
		for(Map.Entry<STATE_TYPE, Map<EVENT_TYPE, STATE_TYPE>> state : transitions.entrySet()) {
			for(Map.Entry<EVENT_TYPE, STATE_TYPE> stateTransition : state.getValue().entrySet()) {
				dot += "\t" + state.getKey().name() + " -> " + stateTransition.getValue().name() + " [ label=\"" + stateTransition.getKey() + "\" ];\n";
			}
		}
		dot += "}";
		Log.d("FragmentFramework", "Fragment State Transition Diagram (GraphViz dot format)\n" + dot);
	}

	protected abstract void onStateChanged(STATE_TYPE state, EVENT_DATA_TYPE eventData);

	public boolean isContainerFor(View container, Fragment fragment) {
		boolean result = false;
		View fragmentView = fragment.getView();
		if (fragmentView != null) {
			ViewParent parent = fragmentView.getParent();
			result = (container == parent);
		}

//		Log.d("IS CONTAINER FOR", container.getId() + " contains " + fragmentView.getId() + ": " + result);
		return result;
	}

	private void showCurrentState() {
		Log.d("FragmentFramework", "Showing state " + currentState);
		int[] fragmentPriority = fragmentPriorities.get(currentState);
		if (fragmentPriority == null) {
			return;
		}
		// start a new transaction for the replaces - this is necessary so we can remove from other
		//   containers and commit (above) before the replace - android will throw an exception
		//   if we try to replace (changing the parent container of the fragment view) before
		//   the fragment has been removed from previous containers and committed
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		Set<View> seen = new HashSet<>();

		outer: for(int fragmentId : fragmentPriority) {
			Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentId);

			// find which container hosts the fragment
			for(View fragmentContainer : fragmentContainers) {
				if (isContainerFor(fragmentContainer, fragment)) {
					if (!seen.contains(fragmentContainer)) {
						// show this fragment
						tx.show(fragment);

						// hide the other fragments in this container
						for(Fragment otherFragment : getSupportFragmentManager().getFragments()) {
							if (otherFragment != fragment && isContainerFor(fragmentContainer, otherFragment)) {
								tx.hide(otherFragment);
							}
						}
						seen.add(fragmentContainer);
					}
					continue outer;
				}
			}

			// dump fragments
			Log.d("FRAGMENT FRAMEWORK", "Fragment " + fragment.getClass().getSimpleName() + " was asked to be displayed for state " + currentState.name() + " but is not contained in any Fragment Containers in the current configuration");
			Log.d("FRAGMENT FRAMEWORK", "....Current Fragment Containers:");
			for(View fragmentContainer : fragmentContainers) {
				String fragmentsContained = "";
				for(Fragment testFragment : getSupportFragmentManager().getFragments()) {
					if (isContainerFor(fragmentContainer, testFragment))
						fragmentsContained += testFragment.getClass().getSimpleName() + " ";
				}
				Log.d("FRAGMENT FRAMEWORK", "........" + fragmentContainer.getId() + " contains fragments " + fragmentsContained);
			}

			throw new IllegalStateException("Requested fragment not contained");
		}
		tx.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FragmentFramework", "Saving state " + currentState);
		outState.putString("currentState", currentState.name());
	}

	protected void handleEvent(EVENT_TYPE event, EVENT_DATA_TYPE eventData) {
		Log.d("FragmentFramework", "Handling event " + event + " in state " + currentState);
		Map<EVENT_TYPE, STATE_TYPE> stateTransitions = transitions.get(currentState);
		if (stateTransitions == null)
			throw new IllegalStateException("No transitions registered for state " + currentState.name());
		STATE_TYPE nextState = stateTransitions.get(event);
		if (nextState == null) {
			Log.d("STATES", "No transition defined from state " + currentState.name() + " with event " + event.name());
			return;
		}
		currentState = nextState;
		showCurrentState();
		onStateChanged(currentState, eventData);
	}

	@Override
	public void onBackPressed() {
		handleEvent(backEvent, null);  // null means no data
		if (currentState == exitState) {
			super.onBackPressed();
		}
	}
}
