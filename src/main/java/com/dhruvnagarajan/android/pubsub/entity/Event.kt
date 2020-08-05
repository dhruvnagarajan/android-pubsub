package com.dhruvnagarajan.android.pubsub.entity

/**
 * @author dhruvaraj nagarajan
 */
data class Event(
    val payload: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: Int = 200,
    val message: String? = null,
    val source: String = "1"
)