package com.paymentSimple

import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.repositories.UserRepository
import com.paymentSimple.services.UserService
import com.paymentSimple.services.UserServiceImpl
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@SpringBootTest
class UserServiceTests {
    private val userRepository: UserRepository = mockk()
    private val userService: UserService = UserServiceImpl(userRepository)

    @Test
    fun `find user by document should return user when document exists`() = runBlocking {
        val document = "12345678910"
        val expectedUser = buildUser().copy(document = document)
        coEvery { userRepository.findByDocument(any()) } returns Mono.just(expectedUser)

        val result = userService.findUserByDocument(document).block()
        assert(result == expectedUser)
        verify {
            userRepository.findByDocument(any())
        }
    }

    @Test
    fun `createUser should save the user`() = runBlocking {
        val userToSave = buildUser()
        coEvery { userRepository.save(userToSave) } returns userToSave
        val result = userService.createUser(userToSave)
        assert(result == userToSave)
    }

    private fun buildUser(): User =
        User(
            userType = UserType.COMMON,
            id = UUID.randomUUID(),
            email = "teste@email.com",
            balance = BigDecimal.ZERO,
            updatedAt = Instant.now(),
            createdAt = Instant.now(),
            lastName = "lastName",
            firstName = "firstName",
            password = "password",
            document = "123456789"
        )
}