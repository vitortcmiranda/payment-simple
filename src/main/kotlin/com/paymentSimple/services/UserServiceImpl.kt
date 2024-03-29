package com.paymentSimple.services

import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.exceptions.UserAlreadyExists
import com.paymentSimple.exceptions.UserNotFoundException
import com.paymentSimple.repositories.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { UserServiceImpl::class.java }
    }

    override suspend fun findUserByDocument(document: String): Mono<User> {
        return userRepository.findByDocument(document)
    }

    override suspend fun createUser(user: User): User =
        userRepository.findByDocument(user.document).awaitSingleOrNull()?.let { throw UserAlreadyExists() }
            ?: userRepository.save(user).also { logger.info { "Creating user for $user" } }


    override suspend fun findUserById(id: UUID): User? =
        userRepository.findById(id).also { logger.info { "Finding user by id : $id" } } ?: throw UserNotFoundException()

    override suspend fun updateUserBalance(user: User): User =
        userRepository.save(user).also {
            logger.info {
                "Updating userId: ${
                    user.id
                } balance:${user.balance}"
            }
        }

}