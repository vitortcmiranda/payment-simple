package com.paymentSimple

import com.paymentSimple.Common.Companion.buildUser
import com.paymentSimple.domain.user.User
import com.paymentSimple.repositories.UserRepository
import com.paymentSimple.services.UserService
import com.paymentSimple.services.UserServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sf.jsqlparser.util.validation.metadata.DatabaseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@SpringBootTest
class UserServiceTests {
    private val userRepository: UserRepository = mockk()
    private val userService: UserService = UserServiceImpl(userRepository)

    @Test
    fun `should find user by id when user exists`() = runBlocking {
        val user = buildUser()
        coEvery { userRepository.findById(any()) } returns user
        val result = userService.findUserById(UUID.randomUUID())
        assert(result == user)
        coVerify(exactly = 1) { userService.findUserById(any()) }
    }

    @Test
    fun `find user by document should return user when document exists`() = runBlocking {
        val document = "12345678910"
        val expectedUser = buildUser().copy(document = document)
        coEvery { userRepository.findByDocument(any()) } returns Mono.just(expectedUser)

        val result = userService.findUserByDocument(document).block()
        assert(result == expectedUser)
        verify(exactly = 1) {
            userRepository.findByDocument(any())
        }
    }

    @Test
    fun `should save the user`() = runTest {
        val userToSave = buildUser()
        coEvery { userRepository.save(userToSave) } returns userToSave
        val result = userService.createUser(userToSave)

        assert(result == userToSave)
        coVerify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should return empty when no user was found by id`() = runBlocking {
        coEvery { userRepository.findById(any()) } returns null
        val result = userService.findUserById(UUID.randomUUID())
        assertNull(result, "Result should be null when user is not found")
        coVerify(exactly = 1) { userRepository.findById(any()) }
    }

    @Test
    fun `should return empty when no user was found by document`() = runBlocking {
        coEvery { userRepository.findByDocument(any()) } returns Mono.empty()
        val result = userService.findUserByDocument(UUID.randomUUID().toString()).block()
        assertNull(result, "Result should be null when user is not found")
        coVerify(exactly = 1) { userRepository.findByDocument(any()) }
    }

    @Test
    fun `should update user balance`() = runBlocking {
        val userToSave = buildUser()
        val expected = userToSave.copy(balance = BigDecimal.TEN)
        coEvery { userRepository.save(any()) } returns expected
        val result = userService.updateUserBalance(userToSave.copy(balance = BigDecimal.TEN))
        assert(result == expected)
        coVerify(exactly = 1) { userRepository.save(expected) }
    }

    @Test
    fun `should update give an error when user not found while updating`() = runBlocking {
        val userToSave = buildUser()
        val expected = userToSave.copy(balance = BigDecimal.TEN)
        coEvery { userRepository.save(any()) } throws DatabaseException("User not exists")

        val exception = assertThrows<DatabaseException> {
            runBlocking { userService.updateUserBalance(userToSave.copy(balance = BigDecimal.TEN)) }
        }
        assertEquals("User not exists", exception.message)
        coVerify(exactly = 1) { userRepository.save(expected) }
    }

    @Test
    fun `should return user by type`() = runBlocking {
        val user = buildUser()
        coEvery { userRepository.findByIdAndType(user.id!!, user.userType) } returns Mono.just(user)

        val result = userService.findUserByIdAndType(user.id!!, user.userType)

        assertEquals(user, result)
        coVerify(exactly = 1) {
            userRepository.findByIdAndType(user.id!!, user.userType)
        }

    }

    @Test
    fun `should return null when no user by type was found`() = runBlocking {
        val user = buildUser()
        coEvery { userRepository.findByIdAndType(user.id!!, user.userType) } returns Mono.empty<User>()

        val result = userService.findUserByIdAndType(user.id!!, user.userType)

        assertEquals(null, result)
        coVerify(exactly = 1) {
            userRepository.findByIdAndType(user.id!!, user.userType)
        }

    }

    @Test
    fun `should return null when no user was found by id`() = runBlocking {
        val user = buildUser()
        coEvery { userRepository.findById(user.id!!) } returns null

        val result = userService.findUserById(user.id!!)

        assertEquals(null, result)
        coVerify(exactly = 1) {
            userRepository.findById(user.id!!)
        }

    }

    @Test
    fun `should return null when no user was found by document`() = runBlocking {
        val user = buildUser()
        coEvery { userRepository.findByDocument(user.document) } returns Mono.empty<User>()

        val result = userService.findUserByDocument(user.document)
        val expected = Mono.empty<User>()
        assertEquals(expected, result)
        coVerify(exactly = 1) {
            userRepository.findByDocument(user.document)
        }

    }

}