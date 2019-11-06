package com.javadude.databinding1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.javadude.databinding1.databinding.ActivityMain2Binding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    class Model {
        fun onAgeClicked(name:String) {
            Log.d("!!!AGE", "clicked $name")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMain2Binding>(this, R.layout.activity_main2)

        val person = Person("Scott", 52)

        binding.person = person
        binding.lifecycleOwner = this
        binding.model = Model()

        var selected = false
        thread {
            while(true) {
                selected = !selected
                binding.selected = selected
                person.name.postValue("${person.name.value}x")
//                person.name = "${person.name}x"
                Thread.sleep(1000)
            }
        }
    }
}
