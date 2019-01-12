package com.javadude.notificationsv2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.NotificationCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val notificationChannel = "com.javadude.foo"

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: SampleViewModel

    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannel, getString(R.string.channel_name), importance)
            channel.description = getString(R.string.channel_description)
            notificationManager.createNotificationChannel(channel)
        }

        viewModel = ViewModelProviders.of(this).get(SampleViewModel::class.java)

        viewModel.isActiveLiveData.observe(this, Observer {
            progressBar2.visibility = if (it == true) View.VISIBLE else View.INVISIBLE
            coroutine_task1_button.isEnabled = it != true
            coroutine_task2_button.isEnabled = it != true
            throw_exception_button.isEnabled = it == true
        })
        viewModel.messageLiveData.observe(this, Observer {
            text.text = it
        })
        viewModel.progressLiveData.observe(this, Observer {
            it?.let {
                progressBar2.progress = it
            }
        })

        toast_button.setOnClickListener {
            Toast.makeText(this, "I am a toast!", Toast.LENGTH_SHORT).show()
        }

        fab.setOnClickListener {
            com.google.android.material.snackbar.Snackbar.make(it, "I am a snackbar!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                    .setAction("Show Dialog", {
                        AlertDialog.Builder(this)
                                .setTitle("Dialog!!!")
//                                .setMessage("Do you want to exit?")
                                .setItems(arrayOf("A", "B", "C"), { _, which ->
                                    Toast.makeText(this, "You selected $which", Toast.LENGTH_SHORT).show()
                                })
                                .setPositiveButton("Yes", { _, _ ->
                                    Toast.makeText(this, "You pressed 'yes'", Toast.LENGTH_SHORT).show()
                                })
                                .setNegativeButton("No", { _, _ ->
                                    Toast.makeText(this, "You pressed 'no'", Toast.LENGTH_SHORT).show()
                                })
                                .show()
                    })
                    .show()
        }

        asynctask1_button.setOnClickListener {
            MyTask1().execute()
        }

        asynctask2_button.setOnClickListener {
            //            MyTask2(viewModel).execute()
            viewModel.runAsyncTask3()
        }

        coroutine_task1_button.setOnClickListener {
            viewModel.runCoroutine1()
        }

        throw_exception_button.setOnClickListener {
            viewModel.throwException = true
        }

        coroutine_task2_button.setOnClickListener {
            viewModel.runCoroutine2 {progress ->
                for (i in 0..99) {
                    progress(i)
                    Log.d("MY COROUTINE 2", "Count = $i")
                    Thread.sleep(25)
                }
            }
        }

        notification1_button.setOnClickListener {
            createNotification(1)
        }

        notification2_button.setOnClickListener {
            createNotification(2)
        }

        cancel1_button.setOnClickListener {
            notificationManager.cancel(1)
        }

        cancel2_button.setOnClickListener {
            notificationManager.cancel(2)
        }

        cancel_all_button.setOnClickListener {
            notificationManager.cancelAll()
        }
    }

    // DO NOT DO THIS!!! DANGER!!! DANGER, WILL ROBINSON!!!
    // DO NOT DO THIS!!! DANGER!!! DANGER, WILL ROBINSON!!!
    // DO NOT DO THIS!!! DANGER!!! DANGER, WILL ROBINSON!!!
    inner class MyTask1 : AsyncTask<Void, Int, Void?>() {
        @UiThread
        override fun onPreExecute() {
            progressBar2.progress = 0
        }
        @WorkerThread
        override fun doInBackground(vararg params: Void?): Void? {
            for(i in 0..99) {
                publishProgress(i)
                Log.d("MY TASK 1", "Count = $i")
                Thread.sleep(25)
            }
            return null
        }

        @UiThread
        override fun onProgressUpdate(vararg values: Int?) {
            progressBar2.progress = values[0]!!
        }

        @UiThread
        override fun onPostExecute(result: Void?) {
            progressBar2.progress = 0
            text.text = "Done!"
        }
    }

    // THIS IS OK - NO IMPLICIT POINTER TO ACTIVITY!
    class MyTask2(val viewModel: SampleViewModel) : AsyncTask<Void, Int, Void?>() {
        @UiThread
        override fun onPreExecute() {
            viewModel.messageLiveData.value = "Running task 2"
            viewModel.isActiveLiveData.value = true
            viewModel.progressLiveData.value = 0
        }
        @WorkerThread
        override fun doInBackground(vararg params: Void?): Void? {
            for(i in 0..99) {
                publishProgress(i)
                Log.d("MY TASK 1", "Count = $i")
                Thread.sleep(25)
            }
            return null
        }

        @UiThread
        override fun onProgressUpdate(vararg values: Int?) {
            viewModel.progressLiveData.value = values[0]!!
        }

        @UiThread
        override fun onPostExecute(result: Void?) {
            viewModel.progressLiveData.value = 0
            viewModel.messageLiveData.value = "Done!"
            viewModel.isActiveLiveData.value = false
        }
    }

    fun createNotification(id : Int) {
        val style = NotificationCompat.InboxStyle()
                .setBigContentTitle("Details")
                .setSummaryText("You've got mail")
                .addLine("Email 1: This is message 1")
                .addLine("Email 2: This is message 2")
                .addLine("Email 3: This is message 3")


        val mainAction = createPending(id, "Jump to app")
        val stopAction = createPending(id + 100, "Stop music")
        val playAction = createPending(id + 200, "Play music")
        val notification =
                NotificationCompat.Builder(this, notificationChannel)
                    .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                    .setColor(Color.BLUE)
                    .setNumber(42)
                    .setAutoCancel(true)
//                    .setOngoing(true)
                    .setStyle(style)
                    .setContentIntent(mainAction)
                    .setContentTitle("Simple Notification $id")
                    .setContentText("This is a simple notification")
                    .addAction(R.drawable.ic_play_arrow_black_24dp, "Play", playAction)
                    .addAction(R.drawable.ic_stop_black_24dp, "Stop", stopAction)
                    .build()
        notificationManager.notify(id, notification)
    }

    private fun createPending(id: Int, message : String): PendingIntent {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("message", message)

        return TaskStackBuilder.create(this)
                .addParentStack(DetailsActivity::class.java)
                .addNextIntent(intent)
                .getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}