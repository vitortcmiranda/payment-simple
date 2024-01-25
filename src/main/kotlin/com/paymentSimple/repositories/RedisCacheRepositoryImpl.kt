package com.paymentSimple.repositories

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisCacheRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>
) : RedisCacheRepository {
    override suspend fun setKey(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    override suspend fun setKey(key: String, value: Any, ttl: Long, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit)
    }

    override suspend fun getKey(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

}