package com.javadude.databinding2.example09

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.facebook.stetho.Stetho
import com.javadude.databinding2.R
import com.javadude.databinding2.databinding.ActivityMain09Binding
import kotlinx.android.synthetic.main.activity_main01.*

class MainActivity : AppCompatActivity() {
    val layout = R.layout.activity_main09

    private val personId1 = "p1"
    private val personId2 = "p2"

    private var currentPersonId : String = personId1
    private lateinit var viewModel : SampleViewModel

    lateinit var binding: ActivityMain09Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, layout)
        binding.lifecycleOwner = this
        binding.model = viewModel
        setSupportActionBar(toolbar)

        viewModel.personId.value = personId1
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                dump.text = viewModel.person.toString()
                true
            }
            R.id.action_swap -> {
                currentPersonId = if (currentPersonId == personId1) {
                    personId2
                } else {
                    personId1
                }
                viewModel.personId.value = currentPersonId
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
