package com.javadude.html2

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class JavaScriptGlue(private val context: Context) {

    @JavascriptInterface
    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
