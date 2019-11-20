package com.javadude.calculator

/**
 * I've separated out the business logic of the calculator from the
 * view model so it can be tested independently (and more quickly)
 *
 * Note that the onDisplayChanged lambda will be used to have the
 *   viewModel update the liveData so we're free of any Android
 *   dependencies
 *
 * There are two (pretty glaring) intentional errors in here...
 *   (Might be something unintentional, but I hope not...)
 */
class CalculatorLogic(val onDisplayChanged: (String) -> Unit) {
    private var startNewNumber = true

    enum class Operation {
        Plus, Minus, Times, Divide, None
    }

    private var currentOperation = Operation.None
    private var currentResult = 0.0
    private var currentDisplay = "0.0"
        set(value) {
            field = value
            onDisplayChanged(value)
        }

    private fun computeThenStart(nextOperation: Operation) {
        when (currentOperation) {
            Operation.Plus -> currentResult += currentDisplay.toDouble()
            Operation.Minus -> currentResult += currentDisplay.toDouble()
            Operation.Times -> currentResult *= currentDisplay.toDouble()
            Operation.Divide -> currentResult /= currentDisplay.toDouble()
            Operation.None -> currentResult = currentDisplay.toDouble()
        }
        currentDisplay = currentResult.toString()
        currentOperation = nextOperation
        startNewNumber = true
    }

    fun negate() {
        currentDisplay = if (currentDisplay.startsWith('-')) {
            currentDisplay.substring(1)
        } else {
            "-$currentDisplay"
        }
    }
    fun equals() {
        computeThenStart(Operation.None)
    }
    fun plus() {
        computeThenStart(Operation.Plus)
    }
    fun minus() {
        computeThenStart(Operation.Minus)
    }
    fun times() {
        computeThenStart(Operation.Times)
    }
    fun divide() {
        computeThenStart(Operation.Divide)
    }
    fun decimal() {
        if (!currentDisplay.contains('.')) {
            currentDisplay += '.'
        }
    }
    fun addDigit(n: Char) {
        if (startNewNumber) {
            currentDisplay = n.toString()
            startNewNumber = false
        } else {
            currentDisplay += n
        }
    }
    fun removeDigit() {
        val temp = currentDisplay.dropLast(2)
        currentDisplay = if (temp.isEmpty()) {
            "0.0"
        } else {
            temp
        }
    }
    fun clearEntry() {
        currentDisplay = "0.0"
    }
    fun clear() {
        currentResult = 0.0
        currentDisplay = "0.0"
    }
}