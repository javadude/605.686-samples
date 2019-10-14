package com.javadude.servicetaskexample;
import com.javadude.servicetaskexample.TaskStatusTransfer;

interface OnTaskServiceListener {
	void onStatusChanged(in Bundle bundle); // bundle contains TaskStatusTransfer taskProgressInfo
}