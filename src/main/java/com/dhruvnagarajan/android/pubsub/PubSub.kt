package com.dhruvnagarajan.android.pubsub

import android.content.Context
import androidx.room.Room
import com.dhruvnagarajan.android.pubsub.entity.Event
import com.dhruvnagarajan.android.pubsub.entity.Topic
import com.dhruvnagarajan.android.pubsub.persistence.DB
import com.dhruvnagarajan.android.pubsub.repository.TopicRepositoryImpl
import com.dhruvnagarajan.android.pubsub.usecase.CreateEventUseCase
import com.dhruvnagarajan.android.pubsub.usecase.GetEventUseCase
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.Subject

/**
 * @author dhruvaraj nagarajan
 */
class PubSub(private val context: Context) : PublicationInteractor {

    private val db = Room.databaseBuilder(
        context,
        DB::class.java,
        DB::class.java.name
    ).build()
    private val metaDao = db.topicMetaDao()
    private val topicRepository = TopicRepositoryImpl(context)
    private val createEventUseCase = CreateEventUseCase(topicRepository)
    private val getEventUseCase: GetEventUseCase = GetEventUseCase(topicRepository)

    private val topics = HashMap<String, Topic>()

    private fun createTopic(
        topicName: String,
        ttl: Topic.TTL = Topic.TTL.REMOVE_ON_CONSUMPTION
    ): Topic {
        if (topics[topicName] != null) return topics[topicName]!!
        val topic = Topic(topicName, ttl, this)
        topics[topicName] = topic
        return topic
    }

    @Throws(NullPointerException::class)
    fun getTopic(topicName: String): Topic {
        if (topics[topicName] == null) throw NullPointerException("Topic not found")
        return topics[topicName]!!
    }

    fun post(topicName: String, event: Event) = createTopic(topicName).post(event)

    /**
     * Retrieves old [Event]s conforming to the parameters.
     * Also is aware of new [Event]s sent to the topic.
     * Always returns existing events from topic before sending out new events,
     * so the arrival of events is predictable.
     *
     * @param pageIndex provides ability to skip pages
     */
    fun get(topicName: String, pageIndex: Int = 0, perPage: Int = 10): Flowable<Event> =
        createTopic(topicName).get(pageIndex, perPage)

    // private api
    override fun createEvent(topic: Topic, event: Event): Single<Event> =
        createEventUseCase.createEvent(CreateEventUseCase.Params(topic, event))

    // private api
    override fun getEvents(
        pageIndex: Int,
        perPage: Int,
        receiverSubject: Subject<Event>
    ): Flowable<Event> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

interface PublicationInteractor {

    fun createEvent(topic: Topic, event: Event): Single<Event>

    fun getEvents(
        pageIndex: Int = 0,
        perPage: Int = 10,
        receiverSubject: Subject<Event>
    ): Flowable<Event>
}