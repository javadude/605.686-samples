package com.javadude.services2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

// implementation of our AIDL-based service
// the binder object we define implements the AIDL interface by subclassing its generated stub
// note that this looks almost identical to the local service binder implementation...
class RemoteService2Impl : Service() {
    @Volatile var i = 1

    private var binder = object : RemoteService2.Stub() {
        override fun reset() {
            i = 1
        }
        override fun add(reporter : RemoteService2Reporter) {
            reporters.add(reporter)
        }
        override fun remove(reporter : RemoteService2Reporter) {
            reporters.remove(reporter)
        }
    }

    private val reporters = mutableListOf<RemoteService2Reporter>()

    private var counterThread : Thread? = null

    private fun createCounter() = object : Thread() {
        override fun run() {
            while(!isInterrupted && i <= 100) {
                Log.d("StartedService", "count = $i")
                val people = listOf(
                    Person("Scott", 10 + i),
                    Person("Steve", 5 + i)
                )
                reporters.forEach {
                    it.report(people, i)
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
        return binder
    }
}