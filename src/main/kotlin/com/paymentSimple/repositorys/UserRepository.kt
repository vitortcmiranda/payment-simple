package com.paymentSimple.repositorys

import org.springframework.boot.autoconfigure.security.SecurityProperties.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : CoroutineCrudRepository<User, Long> {
    fun findUserById(id: Long): Mono<User>
    fun findUserByDocument(doocument: String): Mono<User>
}