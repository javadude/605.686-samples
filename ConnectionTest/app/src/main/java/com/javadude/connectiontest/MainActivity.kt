package com.javadude.connectiontest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.text
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        thread {
            var n = 1

            while (true) {
                val url = URL("http://javadude.com/aliens/$n.json")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connect()
                try {
                    if (connection.responseCode < 300) {
                        val content = InputStreamReader(connection.inputStream).use { isr ->
                            BufferedReader(isr).use { br ->
                                br.readText()
                            }
                        }
                        // do something with the content
                        runOnUiThread {
                            text.text = content
                        }
                        Thread.sleep(1000)
                        n++
                    } else {
                        break
                    }
                } finally {
                    connection.disconnect()
                }
            }
        }
    }
}
