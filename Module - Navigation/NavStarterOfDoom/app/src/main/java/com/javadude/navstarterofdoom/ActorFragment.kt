package com.javadude.navstarterofdoom


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

/**
 * A simple [Fragment] subclass.
 */
class ActorFragment : BaseFragment(R.layout.fragment_actor) {
    private val args : ActorFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text).text = args.actorId
    }
}
