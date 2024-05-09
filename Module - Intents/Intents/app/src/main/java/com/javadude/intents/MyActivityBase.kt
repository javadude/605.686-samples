package com.javadude.intents

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

abstract class MyActivityBase: AppCompatActivity() {
    protected fun Int.onClick(handler : () -> Unit) =
        findViewById<View>(this).setOnClickListener { handler() }
}
