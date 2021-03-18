package com.paypay.challenge.storage

internal interface Storage {

    fun <T> get(key: String, default: T): T

    fun <T> get(key: String): T?

    fun getAll(): Map<String, *>

    fun set(key: String, value: Boolean)

    fun set(key: String, value: Int)

    fun set(key: String, value: Float)

    fun set(key: String, value: Long)

    fun set(key: String, value: String)

    fun set(key: String, value: Set<String>)

    fun remove(key: String)
}