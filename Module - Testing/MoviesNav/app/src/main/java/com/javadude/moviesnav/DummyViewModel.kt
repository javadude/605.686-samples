package com.javadude.moviesnav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Dummy View Model class that's not used in this app.
 * This is just to demonstrate local unit testing, when we don't require a device for instrumented
 * tests
 *
 * If you have a mix of things that can be tested locally and those that require instrumented
 * tests (like database actions), you may want to separate the function into separate view models
 * or POJOs and delegate to them from the ViewModel. This would allow testing the function that
 * doesn't require instrumentation locally, speeding up the tests
 */
open class DummyViewModel : ViewModel() {
    // dummy properties/functions (unused in app) to demonstrate some testing concepts
    // Here we have a "name" property that is set just like a variable. Whenever it is set,
    //   we also update nameLiveData0 (private, mutable) which is exposed to the outside world as
    //   nameLiveData (immutable)
    // Using a variable like this may make testing and assignment a little simpler, but it's extra
    //   work in the view model
    private val nameLiveData0 = MutableLiveData<String>()
    @Suppress("unused")
    val nameLiveData : LiveData<String> = nameLiveData0
    private var name : String = ""
        set(value) {
            field = value
            nameLiveData0.postValue(value) // note - schedules update to run on UI thread!
        }

    fun changeName(name: String) {
        this.name = name
    }
    fun makeDoctor() {
        if (!this.name.startsWith("Dr. ")) {
            this.name = "Dr. ${this.name}"
        }
    }
}

