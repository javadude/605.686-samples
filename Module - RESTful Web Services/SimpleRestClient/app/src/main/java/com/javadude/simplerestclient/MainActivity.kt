package com.javadude.simplerestclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.json.JSONException
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.Result

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        httpRequest(Method.GET, "http://10.0.2.2:8080/restserver/todo")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val item = TodoItem()
        outState.putParcelable("item", item)
    }
    private fun httpRequest(method: Method, uriString: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val jsonText = withContext(Dispatchers.IO) {
                val url = URL(uriString)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = method.name
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode < 300) {
                    connection.inputStream.use { `in` ->
                        InputStreamReader(`in`).use { isr ->
                            isr.readText()
                        }
                    }
                } else {
                    throw RuntimeException("Bad request code=$responseCode")
                }
            }
//            stuff.text = jsonText

            val json = Json(JsonConfiguration.Stable)
            val items = json.parse(TodoItem.serializer().list, jsonText)

            stuff.text = items.joinToString("\n") { it.name.toString() }
        }
    }
}
