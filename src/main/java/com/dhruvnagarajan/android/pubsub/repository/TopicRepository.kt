package com.dhruvnagarajan.android.pubsub.repository

import com.dhruvnagarajan.android.pubsub.entity.Event
import io.reactivex.Single

/**
 * @author dhruvaraj nagarajan
 */
interface TopicRepository {

    fun <T> getAllEvents(clazz: Class<T>): List<T>

    fun <T> getEventsAfterMostRecentOccurrence(lastEventTimestamp: Long)

    fun <T> getEventsAfterMostRecentOccurrence(lastEvent: Event)

    fun createEvent(event: Event): Single<Event>

    fun deleteEvent(timestamp: Long)
}