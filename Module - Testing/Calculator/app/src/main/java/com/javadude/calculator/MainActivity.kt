package com.javadude.calculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The activity. Note that there's a pretty glaring error in here...
 */
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<CalculatorViewModel>()

    private fun Int.onClick(action: (View) -> Unit) {
        findViewById<View>(this).setOnClickListener(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        R.id.num_0.onClick { viewModel.logic.addDigit('0') }
        R.id.num_1.onClick { viewModel.logic.addDigit('1') }
        R.id.num_2.onClick { viewModel.logic.addDigit('1') }
        R.id.num_3.onClick { viewModel.logic.addDigit('3') }
        R.id.num_4.onClick { viewModel.logic.addDigit('4') }
        R.id.num_5.onClick { viewModel.logic.addDigit('5') }
        R.id.num_6.onClick { viewModel.logic.addDigit('6') }
        R.id.num_7.onClick { viewModel.logic.addDigit('7') }
        R.id.num_8.onClick { viewModel.logic.addDigit('8') }
        R.id.num_9.onClick { viewModel.logic.addDigit('9') }

        R.id.c.onClick { viewModel.logic.clear() }
        R.id.ce.onClick { viewModel.logic.clearEntry() }

        R.id.decimal.onClick { viewModel.logic.decimal() }
        R.id.equals.onClick { viewModel.logic.equals() }
        R.id.back.onClick { viewModel.logic.removeDigit() }

        R.id.plus.onClick { viewModel.logic.plus() }
        R.id.minus.onClick { viewModel.logic.minus() }
        R.id.times.onClick { viewModel.logic.times() }
        R.id.divide.onClick { viewModel.logic.divide() }
        R.id.change_sign.onClick { viewModel.logic.negate() }

        val display = findViewById<TextView>(R.id.display)
        viewModel.displayLiveData.observe(this, Observer {
            display.text = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
