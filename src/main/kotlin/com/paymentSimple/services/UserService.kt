package com.paymentSimple.services

import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import reactor.core.publisher.Mono
import java.util.UUID


interface UserService {
    suspend fun findUserByDocument(document: String): Mono<User>
    suspend fun createUser(user: User): User
    suspend fun findUserById(id: UUID): User?
    suspend fun updateUserBalance(user: User): User

}