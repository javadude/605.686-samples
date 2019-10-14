package com.javadude.servicetaskexample

import android.os.Bundle
import android.util.Log

class SampleTaskService : TaskServiceImpl(
    notificationChannelId  = "com.javadude.sample.notification.channel",
    channelNameResource = R.string.channel_name,
    channelDescriptionResource = R.string.channel_description,
    notificationDrawable = R.drawable.ic_directions_run_black_24dp,
    notificationColor = R.color.colorPrimary,
    applicationNameResource = R.string.app_name,
    activityClass=MainActivity::class
) {

    class UploadTask(runId: String, private val args : Bundle?) :
            Task(runId, R.string.upload_task) {

        override fun run(): Bundle? {
            progress(0, "Initializing...")
            requireNotNull(args)
            val start = args.getInt("start", -1)
            val end = args.getInt("end", -1)

            require(start >= 0)
            require(end >= start)

            val progressStep = 100f / (end-start)
            var progress = 0f

            (start..end).forEach { i ->
                Log.d("!!!TASK", i.toString())
                checkCancel {
                    Log.d("!!!TASK", "canceled")
                    // optional cleanup or just call checkCancel()
                    // will throw CancellationAcknowledgedException to skip rest of processing
                    //   after cleanup
                }
                check(!MainActivity.forceFail) { "Failed!" }
                Thread.sleep(2000)
                progress += progressStep
                progress(progress.toInt(), "Working...", Bundle().apply { putInt("value", i) })
            }
            return Bundle().apply {
                putInt("result", 42)
            }
        }
    }

    override val taskCreators = mapOf(
        UploadTask::class.java.name to { runId: String, args: Bundle? ->
            UploadTask(
                runId,
                args
            )
        }
    )
}

