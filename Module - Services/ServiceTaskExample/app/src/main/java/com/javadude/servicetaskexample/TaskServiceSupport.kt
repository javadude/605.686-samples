package com.javadude.servicetaskexample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlin.reflect.KClass

class AlreadyInProgressException(val taskId : String) : Throwable()

class TaskServiceSupport(
    val activity: AppCompatActivity,
    val serviceClass: KClass<*>,
    val progressBar : ProgressBar? = null,
    val forceFieldView : View? = null,
    val onStatusChanged : (TaskStatus, previouslyUnreported: Boolean) -> Unit) {

    init {
        activity.lifecycle.addObserver(ProgressSupportLifecycleObserver())
    }

    private var binder : TaskService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            binder = null
        }

        override fun onServiceConnected(name: ComponentName?, incomingBinder: IBinder?) {
            binder = TaskService.Stub.asInterface(incomingBinder)
            binder?.setOnTaskServiceListener(onTaskServiceListener)
            // immediately check the existing status and update the UI for it

            binder?.status?.let { status ->
                onTaskServiceListener.onStatusChanged(status)
            }
        }
    }

    val status : TaskStatus
        get() = binder?.status?.toTaskStatus() ?: NoTaskRunning

    fun cancelTask() = binder?.cancelTask() ?: throw IllegalStateException("not bound to service")

    @Throws(AlreadyInProgressException::class)
    fun startTask(taskClass: KClass<*>, args: Bundle?) {
        val taskId = taskClass.java.name
        val result = binder?.startTask(taskId, args) ?: throw IllegalStateException("not bound to service")
        if (result.toTaskStatus() is TaskAlreadyRunning) {
            throw AlreadyInProgressException(taskId)
        }
    }
    inline fun <reified T: Task> startTask(args: Bundle? = null) = startTask(T::class, args)

    private val TaskStatusTransfer.nonNullRunId : String
        get() = runId ?: throw IllegalArgumentException("internal error : no run id sent from service")

    private fun Bundle.toTaskStatus() = getParcelable<TaskStatusTransfer>("taskStatus")?.toTaskStatus()

    private fun TaskStatusTransfer.toTaskStatus() =
        when (status) {
            TaskStatusTransfer.NO_SUCH_TASK     -> NoSuchTask(taskId, taskName ?: "(no name)")
            TaskStatusTransfer.ALREADY_RUNNING  -> TaskAlreadyRunning(taskId, taskName ?: "(no name)", nonNullRunId)
            TaskStatusTransfer.RUNNING          -> TaskRunning(taskId, taskName ?: "(no name)", nonNullRunId, progress, message, data)
            TaskStatusTransfer.ERROR            -> TaskFailed(taskId, taskName ?: "(no name)", nonNullRunId, progress, message, throwableStackTrace)
            TaskStatusTransfer.INCOMPLETE       -> TaskIncomplete(taskId, taskName ?: "(no name)", nonNullRunId, progress)
            TaskStatusTransfer.COMPLETE         -> TaskComplete(taskId, taskName ?: "(no name)", nonNullRunId, message, data)
            TaskStatusTransfer.CANCELED         -> TaskCanceled(taskId, taskName ?: "(no name)", nonNullRunId)
            TaskStatusTransfer.STARTING         -> TaskStarting(taskId, taskName ?: "(no name)", nonNullRunId)
            else -> throw IllegalStateException("service returned unknown status $status")
        }

    private val onTaskServiceListener = object : OnTaskServiceListener.Stub() {
        override fun onStatusChanged(bundle : Bundle) {
            // There are issues with passing abstract parcelables across AIDL, so we must put it in a Bundle
            // handle some states explicitly to deal with the progress bar
            bundle.getParcelable<TaskStatusTransfer>("taskStatus")?.let { taskStatusTransfer ->
                val taskStatus = taskStatusTransfer.toTaskStatus()
                activity.runOnUiThread {
                    when (taskStatus) {
                        is TaskRunning -> {
                            forceFieldView?.visibility = View.VISIBLE
                            progressBar?.visibility = View.VISIBLE
                            progressBar?.progress = taskStatus.progress
                        }
                        else -> {
                            forceFieldView?.visibility = View.GONE
                            progressBar?.progress = 0
                        }
                    }
                }
                onStatusChanged(taskStatus, false)
            }
        }
    }

    private inner class ProgressSupportLifecycleObserver : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setupForceField() {
            forceFieldView?.setOnClickListener { /* absorb all clicks (except its child views) */ }
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun bindService() {
            val intent = Intent(activity, serviceClass.java)
            activity.startService(intent)
            activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT)
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun unbindService() {
            activity.unbindService(serviceConnection)
        }
    }
}


