package com.dhruvnagarajan.android.pubsub.producer

import com.dhruvnagarajan.android.pubsub.entity.Topic

/**
 * @author dhruvaraj nagarajan
 */
class Producer<T : Any>(private val topic: Topic) {

    fun publish(t: T) {
//        topic.get().onNext(t)
    }

    private fun cache(t: T) {

    }
}