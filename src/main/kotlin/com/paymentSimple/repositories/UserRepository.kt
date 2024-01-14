package com.paymentSimple.repositories

import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository : CoroutineCrudRepository<User, UUID> {
    fun findByDocument(document: String): Mono<User>

    @Query("select * from users where id = :id and user_type= :type limit 1")
    fun findByIdAndType(id: UUID, type: UserType): Mono<User>
}