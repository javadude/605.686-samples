package com.javadude.files

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var textView : TextView
    enum class Tasks {
        WriteToAppStorage,
        ReadFromAppStorage,
        WriteToAppStorageOnSDCard,
        WriteToMusicStorageOnSDCard
    }

    private val task = Tasks.WriteToMusicStorageOnSDCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.text)

        when (task) {
//            /data/data/com.javadude.files/files
            Tasks.WriteToAppStorage -> {
                File(filesDir, "sample1a.txt").printWriter(StandardCharsets.UTF_8).use { pw ->
                    pw.println("This is line 1a")
                    pw.println("This is line 2a")
                    pw.println("This is line 3a")
                    pw.println("This is line 4a")
                }

                // if it's straight text like that, you can use writeText with a raw string
                File(filesDir, "sample1a.txt").writeText("""
                    |This is line 1a
                    |   This is line 2a
                    |This is line 3a
                    |      This is line 4a
                """.trimMargin(), StandardCharsets.UTF_8)

//                // If you want to be _really_ thorough to protect files,
//                //   explicitly mention each separately so they can be closed
//                //   separately on error. I'd recommend this if you're using your own
//                //   writer types on the outside in case there's an error
//                openFileOutput("sample1b.txt", Context.MODE_PRIVATE).use { fos ->
//                    PrintWriter(fos).use {pw ->
//                        pw.println("This is line 1b")
//                        pw.println("This is line 2b")
//                        pw.println("This is line 3b")
//                        pw.println("This is line 4b")
//                    }
//                }
            }

            Tasks.ReadFromAppStorage -> {
//                openFileInput("sample1a.txt").use {fis ->
//                    InputStreamReader(fis, StandardCharsets.UTF_8).useLines {
//                        textView.text = it.joinToString("\n")
//                    }
//                }
//
//                // or read the entire file in at once
//                openFileInput("sample1a.txt").use {fis ->
//                    textView.text = InputStreamReader(fis, StandardCharsets.UTF_8).readText()
//                }

                textView.text = File(filesDir, "sample1a.txt").readText(StandardCharsets.UTF_8)
            }

            Tasks.WriteToAppStorageOnSDCard -> {
                val externalStorageState = Environment.getExternalStorageState()
                if (Environment.MEDIA_MOUNTED == externalStorageState) {
//                    /Android/data/com.javadude.files
                    File(getExternalFilesDir(null), "sample2.txt").writeText("""
                        This is line 21
                        This is line 22
                        This is line 23
                        This is line 24
                        
                    """.trimIndent(), StandardCharsets.UTF_8)
                }
            }

            Tasks.WriteToMusicStorageOnSDCard -> {
                runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    val externalStorageState = Environment.getExternalStorageState()
                    if (Environment.MEDIA_MOUNTED == externalStorageState) {
                        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "sample3.txt")
                        Log.d("FILE!!!", "exists = ${file.exists()}")
                        val file2 = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "foo/fee/fie/Foo.txt")
                        file2.parentFile?.mkdirs()
                        file.writeText("""
                            This is line 31
                            This is line 32
                            This is line 33
                            This is line 34
                            
                        """.trimIndent(),StandardCharsets.UTF_8)
                        file2.writeText("""
                            |    This is line 31
                            |This is line 32
                            | This is line 33
                            |  This is line 34
                            |
                        """.trimMargin(),StandardCharsets.UTF_8)
                    }
                }
            }
        }
    }
}
