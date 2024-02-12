package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.dto.TransactionValidation
import com.paymentSimple.exceptions.TransactionUnexpectedErrorException
import com.paymentSimple.external.NotificationSenderRepository
import com.paymentSimple.repositories.RedisCacheRepository
import com.paymentSimple.repositories.TransactionsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.TimeUnit


@Service
class TransactionServiceImpl(
    private val userService: UserService,
    private val transactionsRepository: TransactionsRepository,
    private val notificationSenderRepository: NotificationSenderRepository,
    private val redisCacheRepository: RedisCacheRepository,
    private val validations: Validations,
) : TransactionService {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { TransactionServiceImpl::class.java }
    }

    override suspend fun send(transaction: Transactions): Transactions = coroutineScope {
        val transactionValidated = validations.execute(transaction)

        try {
            executeTransaction(transactionValidated)
        }catch (ex: Exception){
            throw TransactionUnexpectedErrorException()
        }


        launch {
            redisCacheRepository.setKey(
                "transaction::${transaction.senderID}::${transaction.amount}",
                "${transaction.receiverID}",
                5,
                TimeUnit.MINUTES
            )
            notificationSenderRepository.sendNotification(
                listOf(
                    transactionValidated.sender,
                    transactionValidated.receiver
                )
            )
            logger.info { "Transaction made by ${transaction.senderID} for ${transaction.receiverID} with amount: ${transaction.amount}" }
        }


        return@coroutineScope transaction
    }

    @Transactional
    private suspend fun executeTransaction(transactionValidated: TransactionValidation) {
        userService.updateUserBalance(transactionValidated.sender.copy(balance = transactionValidated.sender.balance - transactionValidated.amount))
        userService.updateUserBalance(transactionValidated.receiver.copy(balance = transactionValidated.receiver.balance + transactionValidated.amount))
        transactionsRepository.save(
            Transactions(
                senderID = transactionValidated.sender.id!!,
                amount = transactionValidated.amount,
                receiverID = transactionValidated.receiver.id!!,
                updatedAt = Instant.now(),
                createdAt = Instant.now()
            )
        )

    }
}