package com.javadude.activitiesv2

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var n : Int = 0

    /**
     * A LifeCycleObserver that automatically starts and stops a counting
     *   thread based on the lifecycle owner it's attached to. This allows
     *   us to ensure that a resource or thread is properly managed, regardless
     *   if the user remembers to clean up in onPause()
     * This particular observer updates the text on the screen as it counts
     */
    inner class Updater : LifecycleObserver {
        private var updateThread : UpdateThread? = null

        // when the observed lifecycle owner resumes, create and start the thread
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun start() {
            updateThread = UpdateThread()
            updateThread!!.start()
        }

        // when the observed lifecycle owner pauses, stop the thread
        @Suppress("unused")
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun stop() {
            updateThread?.interrupt()
            updateThread = null
        }

        /** the counting thread - doesn't do anything interesting here,
         *  just used to show that you need to ensure you stop it or it
         *  may keep going after the user exits the activity
         */
        inner class UpdateThread : Thread() {
            override fun run() {
                while(!isInterrupted) {
                    updateText()
                    Log.d("THREAD", n.toString())
                    try {
                        sleep(1000)
                    } catch (e: InterruptedException) {
                        interrupt()
                    }
                }
            }
        }
    }

    fun updateText() {
        val newText = "${edit_text.text} $n"
        runOnUiThread { text.text = newText }
        n++
    }

    /** called when the activity is ready for you to create its UI */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MY ACTIVITY", "onCreate")

        // inflate the layout
        setContentView(R.layout.activity_main)

        // if there is data passed in, this means that the activity is
        //   being recreated after a configuration change. This data was
        //   set in onSaveInstanceState before the previous instance of
        //   the activity was destroyed
        if (savedInstanceState !== null) {
            n = savedInstanceState.getInt("n")
        }

        // set a listener on the button in the layout to update the
        //   text whenever it changes
        // NOTE: we're using the android kotlin extensions to automatically
        //       find the Button with id "button" in the layout
        button.setOnClickListener { updateText() }

        // attach the lifecycle observer to this activity. Activities are
        //    lifecycle owners, and will notify the observer of changes in its
        //    lifecycle
        // because this is a lifecycle observer, it will manage its own resources/
        //   threads, and we don't need to stop it ourselves when we pause the
        //   activity
        lifecycle.addObserver(Updater())
    }

    /**
     * Store the current count across configuration changes, so after this activity
     *   instance has been destroyed and recreated we can start with the same number
     *   we left off
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("n", n)
    }

    /** log that we started - just for informational purposes */
    override fun onStart() {
        Log.d("MY ACTIVITY", "onStart")
        super.onStart()
    }

    /** log that we resumed - just for informational purposes */
    override fun onResume() {
        Log.d("MY ACTIVITY", "onResume")
        super.onResume()
    }

    /** log that we paused - just for informational purposes */
    override fun onPause() {
        Log.d("MY ACTIVITY", "onPause")
        super.onPause()
    }

    /** log that we stopped - just for informational purposes */
    override fun onStop() {
        Log.d("MY ACTIVITY", "onStop")
        super.onStop()
    }

    /** log that we have been destroyed - just for informational purposes */
    override fun onDestroy() {
        Log.d("MY ACTIVITY", "onDestroy")
        super.onDestroy()
    }
}
