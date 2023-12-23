package com.paymentSimple.repositorys

import com.paymentSimple.domain.user.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : CoroutineCrudRepository<User, Long> {
    fun findUserById(id: Long): Mono<User>
    fun findUserByDocument(document: String): Mono<User>
}