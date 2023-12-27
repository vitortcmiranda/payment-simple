package com.paymentSimple.repositorys

import com.paymentSimple.domain.user.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository : CoroutineCrudRepository<User, UUID> {
    fun findUserById(id: UUID): Mono<User>
    fun findUserByDocument(document: String): Mono<User>
}