package com.javadude.moviesnav

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.javadude.moviesnav.db.Movie
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


/**
 * Instrumented test, which will execute on an Android device.
 * Note that the fragment container (setup via launchFragmentInContainer) runs in the REAL
 *   app's process. This means we don't have control over setting up the database (in memory) or
 *   executor... (At least I haven't figured out any way to...)
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class FragmentInstrumentedTest {
    @Test fun testMovieDisplayStateFragment() {
        // define our "expected" object
        val transporter = Movie("m1", "The Transporter", "Jason Statham kicks a guy in the face")

        // create a mock for testing navigation
        val mockNavController = mock(NavController::class.java)                         // TEST THREAD

        // launch the fragment with its expected navigation args
        val args = StateMovieDisplayArgs(transporter.id)                                // TEST THREAD
        val scenario = launchFragmentInContainer<StateMovieDisplay>(args.toBundle())    // TEST THREAD

        scenario.onFragment { fragment ->                                               // TEST THREAD
            // set the mock nav controller
            Navigation.setViewNavController(fragment.requireView(), mockNavController)  // UI THREAD (Robolectric uses same thread...)
            // check the movie selection (set via nav args)
            fragment.viewModel.movieSelectionManager.selections.observeOnce {           // UI THREAD (Robolectric uses same thread...)
                assertEquals(1, it.size)                                        // UI THREAD (Robolectric uses same thread...)
                assertEquals(transporter, it.first())                                   // UI THREAD (Robolectric uses same thread...)
            }
        }

        // check that the title was set properly
        onView(withId(R.id.title)).check(matches(withText(transporter.title)))          // TEST THREAD

        // pretend we're navigating
        scenario.onFragment { fragment ->
            fragment.onEditMovie()                                                      // UI THREAD (Robolectric uses same thread...)
        }
        // check that the proper navigation was requested via the mock
        val action = StateMovieEditDirections.actionEditMovie(transporter.id)           // TEST THREAD
        verify(mockNavController).navigate(action)                                      // TEST THREAD
    }
}