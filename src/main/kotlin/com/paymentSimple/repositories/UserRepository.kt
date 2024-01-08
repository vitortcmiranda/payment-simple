package com.paymentSimple.repositories

import com.paymentSimple.domain.user.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository : CoroutineCrudRepository<User, UUID> {
    fun findByDocument(document: String): Mono<User>
}