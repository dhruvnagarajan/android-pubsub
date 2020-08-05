package com.dhruvnagarajan.android.pubsub.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * @author dhruvaraj nagarajan
 */
inline fun <reified T> String.fromJsonInline(): T = Gson().fromJson(this, T::class.java)

fun <T> String.fromJson(): T {
    val tt: Type = object : TypeToken<T>() {}.type
    return Gson().fromJson(this, tt)
}

fun Any.toJson(): String = Gson().toJson(this)