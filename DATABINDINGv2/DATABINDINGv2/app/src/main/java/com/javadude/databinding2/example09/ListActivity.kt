package com.javadude.databinding2.example09

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.stetho.Stetho
import com.javadude.databinding2.R
import com.javadude.databinding2.databinding.ActivityList09Binding

class ListActivity : AppCompatActivity() {
    val layout = R.layout.activity_list09

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        val viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)
        val binding = DataBindingUtil.setContentView<ActivityList09Binding>(this, layout)
        binding.model = viewModel
        binding.setLifecycleOwner(this)
    }
}