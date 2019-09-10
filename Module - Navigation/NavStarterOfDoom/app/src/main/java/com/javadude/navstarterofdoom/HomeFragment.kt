package com.javadude.navstarterofdoom


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseFragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.movie_button).setOnClickListener {
            navigate(HomeFragmentDirections.actionChooseMovie("m1"))
        }
        view.findViewById<Button>(R.id.actor_button).setOnClickListener {
            navigate(HomeFragmentDirections.actionChooseActor("a1"))
        }
    }
}
