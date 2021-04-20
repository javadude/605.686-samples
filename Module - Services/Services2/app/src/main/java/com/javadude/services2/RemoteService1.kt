package com.javadude.services2

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import java.lang.ref.WeakReference

// implementation of our Messenger/Handler-based remote service
class RemoteService1 : Service() {
    private val reporters = mutableListOf<Messenger>()
    private val messenger = Messenger(MyHandler(this))
    @Volatile var i = 1

    fun reset() {
        i = 1
    }
    fun addReporter(reporter : Messenger) {
        reporters.add(reporter)
    }
    fun removeReporter(reporter : Messenger) {
        reporters.remove(reporter)
    }

    private class MyHandler(remoteService1: RemoteService1) : Handler(Looper.getMainLooper()) {
        private val serviceRef = WeakReference(remoteService1)

        override fun handleMessage(msg: Message) {
            serviceRef.get()?.let {
                when (msg.what) {
                    RESET -> it.reset()
                    ADD_REPORTER -> it.addReporter(msg.replyTo)
                    REMOVE_REPORTER -> it.removeReporter(msg.replyTo)
                    else -> super.handleMessage(msg)
                }
            }
        }
    }

    private var counterThread : Thread? = null

    private fun createCounter() = object : Thread() {
        override fun run() {
            while(!isInterrupted && i <= 100) {
                Log.d("StartedService", "count = $i")
                val message = Message.obtain(null, REPORT, i, 0)
                reporters.forEach {
                    it.send(message)
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

    override fun onBind(p0: Intent?): IBinder? {
        if (counterThread == null) {
            counterThread = createCounter()
            counterThread!!.start()
        }
        return messenger.binder
    }

    companion object {
        const val RESET = 1
        const val ADD_REPORTER = 2
        const val REMOVE_REPORTER = 3
        const val REPORT = 100
    }
}
