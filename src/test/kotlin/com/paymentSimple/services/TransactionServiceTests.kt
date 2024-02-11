package com.paymentSimple.services

import com.paymentSimple.Common.Companion.buildTransaction
import com.paymentSimple.Common.Companion.buildUser
import com.paymentSimple.api.MessageApproval
import com.paymentSimple.api.TransactionApprovalResponse
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.dto.TransactionValidation
import com.paymentSimple.external.NotificationSenderRepository
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.RedisCacheRepository
import com.paymentSimple.repositories.TransactionsRepository
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
    private val validations: Validations = mockk()
    private val redisCacheRepository: RedisCacheRepository = mockk()
    private val transactionService: TransactionService


    init {
        transactionService = TransactionServiceImpl(
            userService,
            transactionsRepository,
            notificationSenderRepository,
            redisCacheRepository,
            validations
        )
    }

    @Test
    fun `should return transaction when success`() = runBlocking {
        val transactions = buildTransaction()
        val user = buildUser()
        val transactionValidated = TransactionValidation(user, user, transactions.amount)

        coEvery { validations.execute(transactions) } returns transactionValidated
        coEvery { redisCacheRepository.setKey(any(), any(), any(), any()) } returns Unit
        coEvery { notificationSenderRepository.sendNotification(any()) } returns Unit
        coEvery { userService.updateUserBalance(any()) } returns user

        val result = transactionService.send(transactions)

        coVerify(exactly = 1) { validations.execute(any()) }
        coVerify(exactly = 1) { redisCacheRepository.setKey(any(), any(), any(), any()) }
        coVerify(exactly = 2){userService.updateUserBalance(any())}
        coVerify(exactly = 1) { transactionsRepository.save(any()) }
    }

}
