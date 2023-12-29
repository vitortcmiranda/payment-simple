package com.paymentSimple.services

import com.paymentSimple.domain.user.User
import com.paymentSimple.repositorys.UserRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override suspend fun findUserByDocument(document: String): Mono<User> {
        return userRepository.findByDocument(document)
    }

    override suspend fun createUser(user: User): User =
        userRepository.save(user.copy(id = UUID.randomUUID()))

    override suspend fun findUserById(id: UUID): User? = userRepository.findById(id)
}