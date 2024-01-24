package com.paymentSimple.repositories

import java.util.concurrent.TimeUnit

interface RedisCacheRepository {
    suspend fun setKey(key: String, value: Any)

    suspend fun setKey(key: String, value: Any, ttl: Long, timeUnit: TimeUnit)

    suspend fun getKey(key: String): Any?
}