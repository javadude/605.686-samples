package com.javadude.servicetaskexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        var forceFail = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val forceField = findViewById<View>(R.id.force_field)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val status = findViewById<TextView>(R.id.status)
        var cancelOverride : (() -> Unit)? = null

        val taskServiceSupport = TaskServiceSupport(this, SampleTaskService::class, progressBar, forceField) { taskStatus, previouslyUnreported ->
            runOnUiThread {
                var text =
                        if (previouslyUnreported) {
                            cancelOverride = { forceField.visibility = View.GONE }
                            "From a previous run...\n"

                        } else {
                            ""
                        }

                text += when (taskStatus) {
                    is TaskRunning -> "running"
                    is TaskStarting -> "starting"
                    is TaskComplete -> "finished"
                    is TaskCanceled -> "canceled"
                    is TaskAlreadyRunning -> "already running"
                    is NoTaskRunning -> "no task running"
                    is NoSuchTask -> "no such task"
                    is TaskIncomplete -> {
                        val taskName = if (taskStatus.taskNameResource != -1) getString(taskStatus.taskNameResource) else taskStatus.taskId
                        "Warning! Previously run task '$taskName' did not completely run; You or Android may have killed the service while the application was not running."
                    }
                    is TaskFailed -> "failed\n\n${taskStatus.throwableStackTrace}"
                }

                status.text = text
            }
        }

        operator fun Int.invoke(handler : () -> Unit) {
            findViewById<View>(this).setOnClickListener {
                handler()
            }
        }

        fun startEnd(start : Int, end : Int) =
                Bundle().apply {
                    putInt("start", start)
                    putInt("end", end)
                }

        R.id.upload_button {
            forceFail = false
            try {
                taskServiceSupport.startTask<SampleTaskService.UploadTask>(startEnd(1, 10))
            } catch (e : AlreadyInProgressException) {
                status.text = getString(R.string.already_in_progress)
            }
        }
        R.id.upload2_button {
            forceFail = false
            try {
                taskServiceSupport.startTask<SampleTaskService.UploadTask>(startEnd(1, 20))
            } catch (e : AlreadyInProgressException) {
                status.text = getString(R.string.already_in_progress)
            }
        }
        R.id.fail_button {
            forceFail = true
        }
        R.id.status_button {
            status.text = taskServiceSupport.status.toString()
        }
        R.id.status2_button {
            status.text = taskServiceSupport.status.toString()
        }
        R.id.cancel_button {
            cancelOverride?.invoke() ?: taskServiceSupport.cancelTask()
        }
    }
}
