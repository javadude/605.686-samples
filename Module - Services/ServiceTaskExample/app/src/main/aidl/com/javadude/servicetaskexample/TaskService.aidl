package com.javadude.servicetaskexample;
import com.javadude.servicetaskexample.OnTaskServiceListener;
import com.javadude.servicetaskexample.TaskStatusTransfer;

interface TaskService {
	Bundle startTask(in String qualifiedClassName, in Bundle args); // bundle contains TaskStatusTransfer "taskStatus"
	void cancelTask();
	Bundle getStatus();  // bundle contains TaskStatusTransfer "taskStatus"
	void setOnTaskServiceListener(in OnTaskServiceListener listener);
}