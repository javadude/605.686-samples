package com.javadude.navstarterofdoom


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.navigation.NavDirections
import androidx.navigation.findNavController

abstract class BaseFragment(@LayoutRes val layoutId : Int) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutId, container, false)
    }
    fun navigate(action : NavDirections) {
        view?.findNavController()?.navigate(action)
    }
}
