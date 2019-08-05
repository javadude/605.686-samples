package com.javadude.databinding2.example08

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.facebook.stetho.Stetho
import com.javadude.databinding2.R
import com.javadude.databinding2.databinding.ActivityMain08Binding

class ItemActivity : AppCompatActivity() {
    val layout = R.layout.activity_main08

    private lateinit var viewModel : SampleViewModel
    lateinit var binding: ActivityMain08Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)
        viewModel.personId.value = intent.getStringExtra("id")
        binding = DataBindingUtil.setContentView(this, layout)
        binding.lifecycleOwner = this
        binding.model = viewModel
    }
}
