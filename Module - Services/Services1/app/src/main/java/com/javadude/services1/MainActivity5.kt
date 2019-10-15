package com.javadude.services1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.javadude.services2.Person
import com.javadude.services2.RemoteService2
import com.javadude.services2.RemoteService2Reporter
import kotlinx.android.synthetic.main.activity_main.*

// Activity that binds to a remove service (defined in Services2) using AIDL
// NOTE: The AIDL and any parcelables have to be available on both ends!
//       (we could put them in a common module that both depend upon)
// The reporter implements the AIDL reporting interface by extending its generated stub
class MainActivity5 : AppCompatActivity() {

    private val reporter = object : RemoteService2Reporter.Stub() {
        override fun report(people: List<Person>, i: Int) {
            runOnUiThread {
                progressBar.progress = i
                textView.text = people.joinToString("\n") {
                    "${it.name}: ${it.age}"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reset_button.setOnClickListener {
            binder?.reset()
        }

        other_activity_button.setOnClickListener {
            startActivity(Intent(this, MainActivity3a::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent().apply {
            // Need to specify a different application's ID here
            setClassName("com.javadude.services2", "com.javadude.services2.RemoteService2Impl")
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        binder?.remove(reporter)
        unbindService(serviceConnection)
        super.onStop()
    }


    private var binder : RemoteService2? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            this@MainActivity5.binder = null
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            this@MainActivity5.binder = RemoteService2.Stub.asInterface(binder)
            this@MainActivity5.binder!!.add(reporter)
        }
    }
}

