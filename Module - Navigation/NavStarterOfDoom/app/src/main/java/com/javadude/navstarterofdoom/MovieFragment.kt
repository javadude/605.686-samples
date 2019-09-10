package com.javadude.navstarterofdoom


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

/**
 * A simple [Fragment] subclass.
 */
class MovieFragment : BaseFragment(R.layout.fragment_movie) {
    private val args : MovieFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text).text = args.movieId
    }
}
