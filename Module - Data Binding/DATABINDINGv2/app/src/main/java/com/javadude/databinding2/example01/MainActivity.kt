package com.javadude.databinding2.example01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.javadude.databinding2.R
import kotlinx.android.synthetic.main.activity_main01.*
import kotlinx.android.synthetic.main.address_form01.*

class MainActivity : AppCompatActivity() {
    val layout = R.layout.activity_main01

    private val person1 = Person("Scott", 51,
            Address("123 Sesame Street",
                    "Laurel",
                    "MD",
                    "20723"))

    private val person2 = Person("Alex", 24,
            Address("11 Animated Lane",
                    "New York",
                    "NY",
                    "10011"))

    private var currentPerson : Person = person1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolbar)

        showPerson(person1)
    }

    private fun showPerson(person : Person) {
        currentPerson = person
        name.setText(person.name)
        age.setText(person.age.toString())
        street.setText(person.address.street)
        city.setText(person.address.city)
        state.setText(person.address.state)
        zip.setText(person.address.zip)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                currentPerson.name = name.text.toString()
                currentPerson.age = age.text.toString().toInt()
                currentPerson.address.street = street.text.toString()
                currentPerson.address.city = city.text.toString()
                currentPerson.address.state = state.text.toString()
                currentPerson.address.zip = zip.text.toString()

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
