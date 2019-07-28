package com.javadude.simplerestclient

data class Result(
    val statusCode: Int,
    val statusMessage: String,
    val content: String)
