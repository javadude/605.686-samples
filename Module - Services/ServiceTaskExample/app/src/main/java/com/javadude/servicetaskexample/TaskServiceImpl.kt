package com.javadude.servicetaskexample

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.KClass

val Throwable.stackTraceString : String
    get() {
        val sw = StringWriter()
        PrintWriter(sw).use {
            printStackTrace(it)
        }
        return sw.toString()
    }

class CancellationAcknowledgedException : Throwable()

abstract class Task(
    val runId : String,
    @StringRes val name : Int
) {
    internal var onProgress : ((Int, String?, Bundle?) -> Unit)? = null
    @Volatile var isCanceled = false

    @Throws(CancellationAcknowledgedException::class)
    abstract fun run() : Bundle?

    @Throws(CancellationAcknowledgedException::class)
    fun checkCancel(cleanupBeforeCanceled: () -> Unit = {}) {
        if (isCanceled) {
            cleanupBeforeCanceled()
            throw CancellationAcknowledgedException()
        }
    }

    fun progress(progress: Int, message: String? = null, data : Bundle? = null) {
        onProgress?.invoke(progress, message, data)
            ?: throw IllegalStateException("Task progress called but service has not yet attached onProgress()")
    }
}

abstract class TaskServiceImpl(
    private val notificationChannelId: String,
    @StringRes private val channelNameResource: Int,
    @StringRes private val channelDescriptionResource: Int,
    @DrawableRes private val notificationDrawable: Int,
    @ColorRes private val notificationColor: Int,
    @StringRes private val applicationNameResource: Int,
    private val activityClass : KClass<out Activity>
) : Service() {

    companion object {
        private const val NOTIFICATION_ID = 42
        private const val PENDING_INTENT_ID = 43
    }

    private val executor = Executors.newSingleThreadExecutor()
    private var onTaskServiceListener : OnTaskServiceListener? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var applicationName : String
    private var somethingBound = false

    protected abstract val taskCreators : Map<String, (runId: String, args: Bundle?) -> Task>

    private var runningTaskStatus : TaskStatusTransfer? = null
        set(value) {
            field = value

            updateNotification()

            onTaskServiceListener?.onStatusChanged(bundleOf(value))

            if (value?.status != TaskStatusTransfer.RUNNING) {
                stopIfNothingBound()
            }
        }

    private var runningTask : Task? = null
        set(value) {
            field = value
            if (value == null) {
                stopIfNothingBound()
            }
        }

    private fun stopIfNothingBound() {
        // in this case, we were unbound but a task was still running
        // at this point we want to stop the service, as it's not running a task and
        //   has no binders
        if (!somethingBound) {
            // if nothing is left running, stop the service BUT leave the notification up
            stopForeground(false)
            stopSelf()
        }
    }

    private fun updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    override fun onCreate() {
        super.onCreate()

        applicationName = getString(applicationNameResource)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                notificationChannelId, getString(channelNameResource), importance)
            channel.description = getString(channelDescriptionResource)
            channel.setSound(null, null) // attempt to disable notification sound
            channel.importance = NotificationManager.IMPORTANCE_LOW
            notificationManager.createNotificationChannel(channel)
        }
    }

    private var binder = object : TaskService.Stub() {
        override fun getStatus(): Bundle = bundleOf(runningTaskStatus)

        override fun startTask(qualifiedClassName: String, args: Bundle?) : Bundle {
            runningTask?.let {
                // do not change the current status; this is only a response being returned...
                return bundleOf(TaskStatusTransfer(it.javaClass.name, getString(it.name), TaskStatusTransfer.ALREADY_RUNNING, it.runId))
            }

            val runId = UUID.randomUUID().toString()

            val taskCreator = taskCreators[qualifiedClassName]
                ?: return bundleOf(TaskStatusTransfer(qualifiedClassName, null, TaskStatusTransfer.NO_SUCH_TASK))

            runningTask = taskCreator(runId, args).apply {
                executor.execute {
                    try {
                        report(qualifiedClassName, getString(name), TaskStatusTransfer.RUNNING, runId, 0)

                        onProgress = { progress, message, data ->
                            report(qualifiedClassName, getString(name), TaskStatusTransfer.RUNNING, runId, progress, message, data)
                        }

                        val results = run()
                        report(qualifiedClassName, getString(name), TaskStatusTransfer.COMPLETE, runId, 0, data=results)

                    } catch (e : CancellationAcknowledgedException) {
                        report(qualifiedClassName, getString(name), TaskStatusTransfer.CANCELED, runId)

                    } catch (e : Throwable) {
                        report(qualifiedClassName, getString(name), TaskStatusTransfer.ERROR, runId, 0, throwableStackTrace=e.stackTraceString)

                    } finally {
                        runningTask = null
                    }
                }
            }

            return bundleOf(report(qualifiedClassName, runningTask?.let { getString(it.name) }, TaskStatusTransfer.STARTING, runId))
        }

        override fun cancelTask() {
            runningTask?.isCanceled = true
            runningTask = null
        }

        override fun setOnTaskServiceListener(listener: OnTaskServiceListener) {
            onTaskServiceListener = listener
        }
    }

    private fun bundleOf(taskStatusTransfer: TaskStatusTransfer?) =
        Bundle().apply { putParcelable("taskStatus", taskStatusTransfer) }

    private fun report(
        taskId : String,
        taskName : String? = null,
        status : Int,
        runId : String? = null,
        progress : Int = 0,
        message : String? = null,
        data : Bundle? = null,
        throwableStackTrace : String? = null
    ) =
        TaskStatusTransfer(taskId, taskName, status, runId, progress, message, data, throwableStackTrace).apply {
            runningTaskStatus = this
        }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // don't try to restart the command if android kills the service as it may confuse the user
        //   if the service starts doing the work after they've re-entered the application and have
        //   started to do something else
        // we're starting as a foreground service, so Android will do its best to keep this service
        //   going
        startForeground(NOTIFICATION_ID, createNotification())
        return START_NOT_STICKY
    }

    private val notificationText : String
        get() = runningTask?.let { "${getString(it.name)} ${runningTaskStatus?.statusString}" } ?: "no tasks running"

    private fun createNotification() : Notification {
        val intent = Intent(applicationContext, activityClass.java)
        val pendingIntent = TaskStackBuilder.create(this)
            .addParentStack(activityClass.java)
            .addNextIntent(intent)
            .getPendingIntent(PENDING_INTENT_ID, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, notificationChannelId)
                .setSmallIcon(notificationDrawable)
                .setColor(ResourcesCompat.getColor(resources, notificationColor, baseContext.theme))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSound(null) // attempt to disable notification sound
                .setContentTitle(notificationText)
                .setProgress(100, runningTaskStatus?.progress ?: 0, false)
                .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        runningTask?.isCanceled = true
    }

    override fun onBind(intent: Intent) : IBinder {
        somethingBound = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        somethingBound = false
        // if we're unbinding and there is no running task, stop the service as we have nothing to do
        if (runningTask == null) {
            stopForeground(true)
            stopSelf()
        }
        return super.onUnbind(intent)
    }
}
