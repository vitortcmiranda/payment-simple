package com.paymentSimple.services

import com.paymentSimple.Common
import com.paymentSimple.api.MessageApproval
import com.paymentSimple.api.TransactionApprovalResponse
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.dto.TransactionValidation
import com.paymentSimple.exceptions.TransactionNotAllowedException
import com.paymentSimple.exceptions.UserNotFoundException
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.RedisCacheRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest
class ValidationServiceTests {
    private val userService: UserService = mockk()
    private val redisCacheRepository: RedisCacheRepository = mockk()
    private val transactionValidatorRepository: TransactionValidatorRepository = mockk()
    private val validationService: Validations

    init {
        validationService = ValidationsServiceImpl(
            userService,
            redisCacheRepository,
            transactionValidatorRepository
        )
    }

    @Test
    fun `should throw error when sender does not have suficient founds`() = runBlocking {
        val transactions = Common.buildTransaction()
        val receiver = Common.buildUser().copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123")

        coEvery { userService.findUserById(any()) } returns receiver

        assertThrows<TransactionNotAllowedException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
    }

    @Test
    fun `should throw error when sender was not found`() = runBlocking {
        val transactions = Common.buildTransaction()
        val receiver = Common.buildUser().copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123")

        coEvery { userService.findUserById(transactions.receiverID) } returns receiver
        coEvery { userService.findUserById(transactions.senderID) } returns null


        assertThrows<UserNotFoundException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
    }

    @Test
    fun `should throw error when receiver was not found`() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123", balance = BigDecimal(1000))

        coEvery { userService.findUserById(transactions.senderID) } returns sender
        coEvery { userService.findUserById(transactions.receiverID) } returns null
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Autorizado
        )
        coEvery { redisCacheRepository.getKey(any()) } returns null

        assertThrows<UserNotFoundException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 2) { userService.findUserById(any()) }
        coVerify(exactly = 1) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 1) { redisCacheRepository.getKey(any()) }

    }

    @Test
    fun `should throw error when transaction was not approved by external `() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123", balance = BigDecimal(1000))

        coEvery { userService.findUserById(transactions.senderID) } returns sender
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Desautorizado
        )

        assertThrows<TransactionNotAllowedException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 1) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 0) { redisCacheRepository.getKey(any()) }

    }

    @Test
    fun `should throw error when cache was found`() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123", balance = BigDecimal(1000))

        coEvery { userService.findUserById(any()) } returns sender
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Autorizado
        )
        coEvery { redisCacheRepository.getKey(any()) } returns "teste"

        assertThrows<TransactionNotAllowedException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 1) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 1) { redisCacheRepository.getKey(any()) }

    }

    @Test
    fun `should throw error when sender is MERCHANT`() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(
                userType = UserType.MERCHANT,
                id = UUID.randomUUID(),
                email = "teste@email2.com",
                document = "123",
                balance = BigDecimal(1000)
            )

        coEvery { userService.findUserById(any()) } returns sender
        assertThrows<TransactionNotAllowedException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 0) { redisCacheRepository.getKey(any()) }

    }

    @Test
    fun `should throw error when sender founds are insuficient`() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(
                userType = UserType.MERCHANT,
                id = UUID.randomUUID(),
                email = "teste@email2.com",
                document = "123",
                balance = BigDecimal(10)
            )

        coEvery { userService.findUserById(any()) } returns sender
        assertThrows<TransactionNotAllowedException> {
            runBlocking { validationService.execute(transactions) }
        }

        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 0) { redisCacheRepository.getKey(any()) }

    }


    @Test
    fun `should return a Transaction validated`() = runBlocking {
        val transactions = Common.buildTransaction()
        val sender = Common.buildUser()
            .copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123", balance = BigDecimal(1000))

        val receiver = Common.buildUser()
            .copy(
                id = UUID.randomUUID(),
                email = "teste@email2.com",
                document = "123",
                balance = BigDecimal(1000),
                userType = UserType.MERCHANT
            )

        coEvery { userService.findUserById(transactions.senderID) } returns sender
        coEvery { userService.findUserById(transactions.receiverID) } returns receiver
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Autorizado
        )
        coEvery { redisCacheRepository.getKey(any()) } returns null

        val result = runBlocking { validationService.execute(transactions) }

        assertEquals(result, TransactionValidation(sender, receiver, transactions.amount))

        coVerify(exactly = 2) { userService.findUserById(any()) }
        coVerify(exactly = 1) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 1) { redisCacheRepository.getKey(any()) }

    }

}