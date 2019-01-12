package com.javadude.databinding2.example05

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.javadude.databinding2.R
import com.javadude.databinding2.databinding.ActivityMain05Binding
import kotlinx.android.synthetic.main.activity_main01.*

class MainActivity : AppCompatActivity() {
    val layout = R.layout.activity_main05

    private val person1 = Person("Scott", 51,
            Address("123 Sesame Street",
                    "Laurel",
                    State.MD,
                    "20723"))

    private val person2 = Person("Alex", 24,
            Address("11 Animated Lane",
                    "New York",
                    State.NY,
                    "10011"))

    private var currentPerson : Person = person1

    lateinit var binding: ActivityMain05Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout)
        setSupportActionBar(toolbar)

        showPerson(person1)
    }

    private fun showPerson(person : Person) {
        currentPerson = person
        binding.person = currentPerson
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                dump.text = currentPerson.toString()
                true
            }
            R.id.action_swap -> {
                if (currentPerson == person1) {
                    showPerson(person2)
                } else {
                    showPerson(person1)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
