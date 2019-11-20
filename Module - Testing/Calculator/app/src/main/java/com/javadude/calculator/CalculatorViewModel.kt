package com.javadude.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * The view model - it exposes a logic object that the application can use
 *   to perform its actions
 */
class CalculatorViewModel : ViewModel() {
    private val displayLiveData0 = MutableLiveData<String>()
    val displayLiveData : LiveData<String> = displayLiveData0

    val logic = CalculatorLogic {
        displayLiveData0.value = it
    }
}