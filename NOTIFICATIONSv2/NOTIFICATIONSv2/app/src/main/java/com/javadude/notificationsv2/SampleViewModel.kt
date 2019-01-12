package com.javadude.notificationsv2

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.PrintWriter
import java.io.StringWriter

class SampleViewModel : ViewModel() {
    val progressLiveData = MutableLiveData<Int>().apply { value = 0 }
    val messageLiveData = MutableLiveData<String>().apply { value = null }
    val isActiveLiveData = MutableLiveData<Boolean>().apply { value = false }

    var throwException = false

    fun runCoroutine1() {
        launch(UI) {
            var caughtException = false
            try {
                // UI setup goes here
                messageLiveData.value = "Running coroutine task 1"
                isActiveLiveData.value = true
                progressLiveData.value = 0

                async(CommonPool) {
                    for (i in 0..99) {
                        if (throwException) {
                            throwException = false
                            throw RuntimeException("You asked to throw an exception!")
                        }
                        progressLiveData.postValue(i)
                        Log.d("MY COROUTINE 1", "Count = $i")
                        Thread.sleep(25)
                    }
                }.await()

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
    }

    fun runCoroutine2(task : (progress : (Int) -> Unit) -> Unit) {
        launch(UI) {
            var caughtException = false
            try {
                // UI setup goes here
                messageLiveData.value = "Running coroutine task 1"
                isActiveLiveData.value = true
                progressLiveData.value = 0

                async(CommonPool) {
                    task(progressLiveData::postValue)
                }.await()

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
    }


    fun runAsyncTask3() {
        MyTask3().execute()
    }
    @SuppressLint("StaticFieldLeak")
    inner class MyTask3 : AsyncTask<Void, Int, Void?>() {
        @UiThread
        override fun onPreExecute() {
            messageLiveData.value = "Running task 2"
            isActiveLiveData.value = true
            progressLiveData.value = 0
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
            progressLiveData.value = values[0]!!
        }

        @UiThread
        override fun onPostExecute(result: Void?) {
            progressLiveData.value = 0
            messageLiveData.value = "Done!"
            isActiveLiveData.value = false
        }
    }
}