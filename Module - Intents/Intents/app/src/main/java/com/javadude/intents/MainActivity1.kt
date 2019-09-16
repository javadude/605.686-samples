package com.javadude.intents

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity1 : AppCompatActivity() {
    companion object {
        private const val DATA_REQUEST = 42
    }
    private lateinit var resultView : TextView

    private fun Int.onClick(handler : () -> Unit) =
        findViewById<View>(this).setOnClickListener { handler() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        resultView = findViewById(R.id.resultText)
        val dataView = findViewById<EditText>(R.id.data)

        R.id.button1.onClick {
            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra("data", dataView.text.toString())
            }

            startActivityForResult(intent, DATA_REQUEST)
        }
        R.id.button2.onClick {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "application/vnd.javadude.data"
                putExtra("id", 42)
            }
            if (intent.resolveActivity(packageManager) == null) {
                Toast.makeText(this,
                    "Sorry - I cannot process that request as no application is installed that can view JavaDude data",
                    Toast.LENGTH_LONG).show()

            } else {
                startActivity(intent)
            }
        }
        R.id.button3.onClick {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(dataView.text.toString()), "text/html")
            }
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DATA_REQUEST) {
            if (resultCode == RESULT_OK) {
                resultView.text = data?.getStringExtra("resultInfo")
            } else {
                resultView.text = "canceled"
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
