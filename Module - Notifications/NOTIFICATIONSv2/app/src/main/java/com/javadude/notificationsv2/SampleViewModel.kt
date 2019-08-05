package com.javadude.notificationsv2

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter

class SampleViewModel : ViewModel() {
    val progressLiveData = MutableLiveData<Int>().apply { value = 0 }
    val messageLiveData = MutableLiveData<String>().apply { value = null }
    val isActiveLiveData = MutableLiveData<Boolean>().apply { value = true }

    var throwException = false

    fun runCoroutine1() = viewModelScope.launch(Dispatchers.Main) {
        var caughtException = false
        try {
            // UI setup goes here
            messageLiveData.value = "Running coroutine task 1"
            isActiveLiveData.value = true
            progressLiveData.value = 0

            withContext(Dispatchers.IO) {
                for (i in 0..99) {
                    if (throwException) {
                        throwException = false
                        throw RuntimeException("You asked to throw an exception!")
                    }
                    withContext(Dispatchers.Main) {
                        progressLiveData.value = i
                    }
                    // OR
                    //     progressLiveData.postValue(i)
                    Log.d("MY COROUTINE 1", "Count = $i")
                    delay(25)
                }
            }

        } catch (t : Throwable) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.close()
            messageLiveData.value = sw.toString()
            caughtException = true

        } finally {
            // UI cleanup goes here
            progressLiveData.value = 0
            if (!caughtException) {
                messageLiveData.value = "Done!"
            }
            isActiveLiveData.value = false
        }
    }

    fun runCoroutine2(task : (setProgress : (Int) -> Unit) -> Unit) = viewModelScope.launch {
        var caughtException = false
        try {
            // UI setup goes here
            messageLiveData.value = "Running coroutine task 2"
            isActiveLiveData.value = true
            progressLiveData.value = 0

            withContext(Dispatchers.Default) {
                task {progress ->
                    progressLiveData.postValue(progress)
                }
//                task(progressLiveData::postValue) // SORT-HAND METHOD REREFERENCE
            }

        } catch (t : Throwable) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.close()
            messageLiveData.value = sw.toString()
            caughtException = true

        } finally {
            // UI cleanup goes here
            progressLiveData.value = 0
            if (!caughtException) {
                messageLiveData.value = "Done!"
            }
            isActiveLiveData.value = false
        }
    }


    fun runAsyncTask3() {
        MyTask3().execute()
    }
    @SuppressLint("StaticFieldLeak")
    inner class MyTask3 : AsyncTask<Void, Int, Void?>() {
        override fun onPreExecute() {
            messageLiveData.value = "Running task 2"
            isActiveLiveData.value = true
            progressLiveData.value = 0
        }
        override fun doInBackground(vararg params: Void?): Void? {
            for(i in 0..99) {
                publishProgress(i)
                Log.d("MY TASK 1", "Count = $i")
                Thread.sleep(25)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            progressLiveData.value = values[0]!!
        }

        override fun onPostExecute(result: Void?) {
            progressLiveData.value = 0
            messageLiveData.value = "Done!"
            isActiveLiveData.value = false
        }
    }
}