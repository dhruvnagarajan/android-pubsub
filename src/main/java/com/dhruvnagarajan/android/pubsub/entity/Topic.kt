package com.dhruvnagarajan.android.pubsub.entity

import com.dhruvnagarajan.android.pubsub.PublicationInteractor
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject

/**
 * @author dhruvaraj nagarajan
 */
class Topic(
    val topicKey: String,
    var ttl: TTL,
    private val publicationInteractor: PublicationInteractor
) {

    private lateinit var disposable: Disposable

    private val subject: Subject<Event> = when (ttl) {
        is TTL.DAY -> ReplaySubject.create()
        is TTL.HOUR -> ReplaySubject.create()
        is TTL.MINUTE -> ReplaySubject.create()
        is TTL.MILLIS -> ReplaySubject.create()
        is TTL.REMOVE_ON_CONSUMPTION -> PublishSubject.create()
        is TTL.NOT_PERSISTED -> PublishSubject.create()
    }

    /**
     * Retrieves old [Event]s conforming to the parameters.
     * Also is aware of new [Event]s sent to the topic.
     * Always returns existing events from topic before sending out new events,
     * so the arrival of events is predictable.
     *
     * @param pageIndex provides ability to skip pages
     */
    fun get(pageIndex: Int = 0, perPage: Int = 10): Flowable<Event> {
        publicationInteractor.getEvents(pageIndex, perPage, subject)
        return subject.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun post(event: Event) {
        val eventCreationDisposable = publicationInteractor.createEvent(this, event)
            .subscribe(
                {
                    subject.onNext(it)
                },
                {
                    subject.onError(it)
                }
            )
    }

    /**
     * set new TTL and remove events from storage where existingTopicEvent.TTL > [ttl]
     */
    fun setNewTTL(ttl: TTL) {
        this.ttl = ttl
//        deleteEventUseCase.performHouseKeeping(this)
    }

    /**
     * Time To Live.
     *
     * topic records are persisted using room.
     * records have to be deleted after a timeout.
     * choose your desired timeout for persistence in this topic from below.
     */
    sealed class TTL(val ttl: Int) {
        data class DAY(val value: Int) : TTL(1000 * 60 * 60 * 24 * value)
        data class HOUR(val value: Int) : TTL(1000 * 60 * 60 * value)
        data class MINUTE(val value: Int) : TTL(1000 * 60 * value)
        data class MILLIS(val value: Int = 1000) : TTL(value)
        object REMOVE_ON_CONSUMPTION : TTL(0)
        object NOT_PERSISTED : TTL(-1)
    }
}