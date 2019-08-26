package com.javadude.html2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                if (uri.host!!.toLowerCase().endsWith("javadude.com")) {
                    return false
                }
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                return true
            }
        }

        // LOAD CONTENT FROM THE WEB
        webView.loadUrl("http://javadude.com")


        // LOAD CONTENT FROM A STRING
        val data = """<html>
                     |  <body>
                     |    <p>Hello <b>there</b></p>
                     |  </body>
                     |</html>""".trimMargin()
//        webView.loadData(data, "text/html","UTF-8")

        // LOAD LOCAL HTML PAGES
//        webView!!.loadUrl("file:///android_asset/html/sample.html")
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
