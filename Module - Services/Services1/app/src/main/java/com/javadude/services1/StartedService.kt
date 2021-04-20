package com.javadude.services1

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.util.Log

// A started service (basically "start and forget")
class StartedService : Service() {
    private val counterThread = object : Thread() {
        override fun run() {
            var i = 0
            while(!isInterrupted && i <= 100) {
                Log.d("StartedService", "count = $i")
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        counterThread.start() // start some processing
        return START_REDELIVER_INTENT // try to re-run service later if killed
    }

    override fun onDestroy() {
        counterThread.interrupt()
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): Binder? = null
}