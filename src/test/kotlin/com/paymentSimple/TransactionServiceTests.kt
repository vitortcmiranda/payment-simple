package com.paymentSimple

import com.paymentSimple.domain.transaction.Transactions
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
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.Instant
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

        assertThrows(ResponseStatusException::class.java) {
            runBlocking {
                transactionService.validateTransaction(transactions)
            }
        }
        coVerify(exactly = 0) { transactionsRepository.save(any()) }
    }

    private fun buildTransaction(): Transactions = Transactions(
        UUID.randomUUID(),
        createdAt = Instant.now(),
        amount = BigDecimal.TEN,
        senderID = UUID.randomUUID(),
        receiverID = UUID.randomUUID(),
        updatedAt = Instant.now()
    )
}
