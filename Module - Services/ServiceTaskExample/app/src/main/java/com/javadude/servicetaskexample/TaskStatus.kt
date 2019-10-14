package com.javadude.servicetaskexample

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class TaskStatus {
    abstract val taskId: String
    abstract val taskName : String
}

/**
 * Indicates that no tasks are running
 */
object NoTaskRunning : TaskStatus() {
    override val taskId = ""
    override val taskName = ""
}

/**
 * Indicates that the task is currently running
 */
data class TaskRunning(
    override val taskId: String,
    override val taskName : String,
    val runId: String,
    val progress: Int,
    val message : String? = null,
    val data : Bundle? = null
) : TaskStatus()

/**
 * Indicates that the task is being started
 */
data class TaskStarting(
    override val taskId: String,
    override val taskName : String,
    val runId: String
) : TaskStatus()

/**
 * Indicates that the task is currently running
 */
data class TaskComplete(
    override val taskId: String,
    override val taskName : String,
    val runId: String,
    val message : String? = null,
    val data : Bundle? = null
) : TaskStatus()

/**
 * Indicates that the task is currently running
 */
data class TaskCanceled(
    override val taskId: String,
    override val taskName : String,
    val runId: String
) : TaskStatus()

/**
 * Indicates that the task is currently running
 */
data class TaskAlreadyRunning(
    override val taskId: String,
    override val taskName : String,
    val runId: String
) : TaskStatus()

/**
 * Indicates that the requested task has not been registered
 */
data class NoSuchTask(
    override val taskId: String,
    override val taskName : String
) : TaskStatus()

/**
 * Indicates that the last run of the task did not complete, likely because the process was
 *   killed before it had a chance to finish
 */
data class TaskIncomplete(
    override val taskId: String,
    override val taskName : String,
    val runId: String,
    val taskNameResource : Int
) : TaskStatus()

/**
 * Indicates that the last run of the task failed
 */
data class TaskFailed(
    override val taskId: String,
    override val taskName : String,
    val runId: String,
    val progress : Int,
    val message : String? = null,
    val throwableStackTrace : String? = null
) : TaskStatus()

@Parcelize
internal data class TaskStatusTransfer(
    val taskId : String,
    val taskName : String? = null,
    val status : Int,
    val runId : String? = null,
    val progress : Int = 0, // also used as task name for incomplete
    val message : String? = null,
    val data : Bundle? = null,
    val throwableStackTrace : String? = null
) : Parcelable {
    companion object {
        const val NOT_RUNNING = 0
        const val NO_SUCH_TASK = 1
        const val RUNNING = 2
        const val ALREADY_RUNNING = 3
        const val ERROR = 4
        const val INCOMPLETE = 5
        const val COMPLETE = 6
        const val CANCELED = 7
        const val STARTING = 8
    }
    val statusString : String
        get() = when(status) {
            NOT_RUNNING -> "not running"
            NO_SUCH_TASK -> "no such task"
            RUNNING -> "running"
            ALREADY_RUNNING -> "already running"
            ERROR -> "error"
            INCOMPLETE -> "incomplete"
            COMPLETE -> "finished"
            CANCELED -> "canceled"
            STARTING -> "starting"
            else -> throw IllegalStateException()
        }
}