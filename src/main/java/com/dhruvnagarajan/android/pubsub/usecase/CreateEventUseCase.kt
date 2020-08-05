package com.dhruvnagarajan.android.pubsub.usecase

import com.dhruvnagarajan.android.pubsub.entity.Event
import com.dhruvnagarajan.android.pubsub.entity.Topic
import com.dhruvnagarajan.android.pubsub.repository.TopicRepository
import io.reactivex.Single

/**
 * @author dhruvaraj nagarajan
 */
class CreateEventUseCase(private val topicRepository: TopicRepository) {

    fun createEvent(params: Params): Single<Event> {
        val hashCode = params.event.payload.hashCode()
        return topicRepository.createEvent(params.event)
    }

    data class Params(
        val topic: Topic,
        val event: Event
    )
}
