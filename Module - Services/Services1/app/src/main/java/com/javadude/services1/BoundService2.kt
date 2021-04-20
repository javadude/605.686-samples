package com.javadude.services1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

// Bound service we can send a command to _and_ add a callback
// NOTE: MUST RUN IN SAME PROCESS AS THE COMPONENT THAT BINDS TO IT
class BoundService2 : Service() {
    @Volatile var i = 1
    inner class BoundServiceBinder : Binder() {
        fun reset() {
            i = 1
        }
        fun addReporter(reporter : Reporter) {
            reporters.add(reporter)
        }
        fun removeReporter(reporter : Reporter) {
            reporters.remove(reporter)
        }
    }

    interface Reporter {
        fun report(i : Int)
    }

    private val reporters = mutableListOf<Reporter>()

    private var counterThread : Thread? = null

    private fun createCounter() = object : Thread() {
        override fun run() {
            while(!isInterrupted && i <= 100) {
                Log.d("StartedService", "count = $i")
                reporters.forEach {
                    it.report(i)
                }
                try {
                    sleep(250)
                } catch (e : InterruptedException) {
                    interrupt()
                }

                i++
            }
            stopSelf()
        }
    }

    override fun onDestroy() {
        counterThread?.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        if (counterThread == null) {
            counterThread = createCounter()
            counterThread!!.start()
        }
        return BoundServiceBinder()
    }
}