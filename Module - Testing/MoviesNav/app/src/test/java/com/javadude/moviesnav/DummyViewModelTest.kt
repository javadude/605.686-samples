package com.javadude.moviesnav

import org.junit.Test

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import java.lang.reflect.Field

/**
 * Example local unit test, which will execute on the development machine (host).
 * We can use function names with embedded spaces if we want... Funky...
 *
 * Note that this test does not run on an emulator, and must use mocked Android classes.
 * Robolectric provides those mocks for us using the AndroidJUnit4 test runner
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DummyViewModelTest {
    // NOTE: the "name" property of the view model is PRIVATE. We normally cannot access it,
    //       but Java reflection allows us to get around that, which is useful for testing.
    //       DO NOT DO THIS FOR ANYTHING OTHER THAN TESTING - it kills encapsulation!
    companion object {
        private lateinit var nameField : Field
        @JvmStatic
        @BeforeClass
        fun findMethods() {
            // lookup the "name" property and mark it accessible
            nameField = DummyViewModel::class.java.getDeclaredField("name")
            nameField.isAccessible = true
        }
    }

    // create an extension "name" property that reads the real, exposed name property
    private val DummyViewModel.name : String
        get() {
            return nameField.get(this) as String
        }

    @Test
    fun `test set name`() {
        val viewModel = DummyViewModel()
        assertEquals("", viewModel.name)
        viewModel.changeName("Scott")
        assertEquals("Scott", viewModel.name)
    }

    // if there is more setup needed for all tests, you can use
    private lateinit var viewModel : DummyViewModel
    @Before
    fun `setup view model`() {
        viewModel = DummyViewModel()
        // other setup
    }

    // and then just use it in tests
    @Test
    fun `test make doctor`() {
        viewModel.changeName("Scott")
        assertEquals("Scott", viewModel.name)
        viewModel.makeDoctor()
        assertEquals("Dr. Scott", viewModel.name)
    }
}
