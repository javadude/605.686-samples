package com.javadude.rest;

public class Result {
	private int statusCode;
	private String statusMessage;
	private String content;

	public Result(int statusCode, String statusMessage, String content) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.content = content;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getContent() {
		return content;
	}
}
