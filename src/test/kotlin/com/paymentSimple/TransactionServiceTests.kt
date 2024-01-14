package com.paymentSimple

import com.paymentSimple.Common.Companion.buildTransaction
import com.paymentSimple.Common.Companion.buildUser
import com.paymentSimple.api.MessageApproval
import com.paymentSimple.api.TransactionApprovalResponse
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.external.NotificationSenderRepository
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.TransactionsRepository
import com.paymentSimple.services.TransactionService
import com.paymentSimple.services.TransactionServiceImpl
import com.paymentSimple.services.UserService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.*

@SpringBootTest
class TransactionServiceTests {

    private val userService: UserService = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val transactionValidatorRepository: TransactionValidatorRepository = mockk()
    private val notificationSenderRepository: NotificationSenderRepository = mockk()
    private val transactionService: TransactionService

    init {
        transactionService = TransactionServiceImpl(
            userService,
            transactionsRepository,
            transactionValidatorRepository,
            notificationSenderRepository
        )
    }

    @Test
    fun `should throw exception when validation encounters errors`() = runBlocking {
        val transactions = buildTransaction()
        coEvery { userService.findUserById(any()) } returns null
        coEvery { userService.findUserByIdAndType(any(), any()) } returns null
        val exception = assertThrows<ResponseStatusException> {
            runBlocking {
                transactionService.validateTransaction(transactions)
            }
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
        coVerify(exactly = 0) { transactionsRepository.save(any()) }
    }

    @Test
    fun `should throw error when sender is merchant`() = runBlocking {
        val sender = buildUser().copy(userType = UserType.MERCHANT)
        val receiver = buildUser().copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123")
        coEvery { userService.findUserByIdAndType(any(), any()) } returns sender
        coEvery { userService.findUserById(any()) } returns receiver

        val transactions = buildTransaction()
        val exception = assertThrows<ResponseStatusException> {
            runBlocking { transactionService.validateTransaction(transactions) }
        }

        assertEquals("Merchant user not allowed to send money", exception.reason)
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, exception.statusCode)
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
        coVerify(exactly = 1) { userService.findUserByIdAndType(any(), any()) }
        coVerify(exactly = 1) { userService.findUserById(any()) }

    }

    @Test
    fun `should throw error when sender was not found`() = runBlocking {
        val transactions = buildTransaction()
        val receiver = buildUser().copy(id = UUID.randomUUID(), email = "teste@email2.com", document = "123")

        coEvery { userService.findUserByIdAndType(any(), any()) } returns null
        coEvery { userService.findUserById(any()) } returns receiver

        val exception = assertThrows<ResponseStatusException> {
            runBlocking { transactionService.validateTransaction(transactions) }
        }
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User not found", exception.reason)
        coVerify(exactly = 1) { userService.findUserByIdAndType(any(), any()) }
        coVerify(exactly = 1) { userService.findUserById(any()) }
        coVerify(exactly = 0) { transactionValidatorRepository.validateTransaction(any()) }
    }

    @Test
    fun `should throw error when sender balance is less than amount transaction`() = runBlocking {

        val sender = buildUser().copy(userType = UserType.COMMON, balance = BigDecimal(30))
        val receiver = buildUser()
        val transaction =
            buildTransaction().copy(amount = BigDecimal(50), senderID = sender.id!!, receiverID = receiver.id!!)
        coEvery { userService.findUserByIdAndType(any(), any()) } returns sender
        coEvery { userService.findUserById(any()) } returns receiver

        val exception = assertThrows<ResponseStatusException> {
            transactionService.validateTransaction(transaction)
        }

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, exception.statusCode)
        assertEquals("User doesn't have sufficient funds", exception.reason)

    }

    @Test
    fun `should throw error when third party don't approve`() = runBlocking {
        val sender = buildUser().copy(firstName = "sender", balance = BigDecimal(50))
        val receiver = buildUser().copy(firstName = "receiver")
        val transaction =
            buildTransaction().copy(amount = BigDecimal(20), senderID = sender.id!!, receiverID = receiver.id!!)
        coEvery { userService.findUserByIdAndType(any(), any()) } returns sender
        coEvery { userService.findUserById(any()) } returns receiver
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Desautorizado
        )
        val exception = assertThrows<ResponseStatusException> {
            transactionService.validateTransaction(transaction)
        }

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, exception.statusCode)
        assertEquals("Transaction Not Allowed by external approval", exception.reason)

    }

    @Test
    fun `should return sender when validated`() = runBlocking {
        val sender = buildUser().copy(firstName = "sender", balance = BigDecimal(50))
        val receiver = buildUser().copy(firstName = "receiver")
        val transaction =
            buildTransaction().copy(amount = BigDecimal(20), senderID = sender.id!!, receiverID = receiver.id!!)
        coEvery { userService.findUserByIdAndType(any(), any()) } returns sender
        coEvery { userService.findUserById(any()) } returns receiver
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Autorizado
        )
        val expected = listOf(receiver, sender)
        val result = transactionService.validateTransaction(transaction)
        assertEquals(expected, result)
    }

    @Test
    fun `should return transaction when successfully transaction was sent`() = runBlocking {
        val sender = buildUser().copy(firstName = "sender", balance = BigDecimal(50))
        val receiver = buildUser().copy(firstName = "receiver")
        val transaction =
            buildTransaction().copy(amount = BigDecimal(20), senderID = sender.id!!, receiverID = receiver.id!!)
        coEvery { userService.findUserByIdAndType(any(), any()) } returns sender
        coEvery { userService.findUserById(any()) } returns receiver
        coEvery { transactionValidatorRepository.validateTransaction(any()) } returns TransactionApprovalResponse(
            MessageApproval.Autorizado
        )
        coEvery { userService.updateUserBalance(any()) } returns sender
        coEvery { transactionsRepository.save(any()) } returns transaction

        coEvery { notificationSenderRepository.sendNotification(any()) } returns Unit

        val result = transactionService.send(transaction)

        assertEquals(transaction, result)
    }

}
