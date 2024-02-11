package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.external.NotificationSenderRepository
import com.paymentSimple.repositories.RedisCacheRepository
import com.paymentSimple.repositories.TransactionsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    override suspend fun send(transaction: Transactions): Transactions = coroutineScope {
        val transactionValidated = validations.execute(transaction)


        val sender =
            userService.updateUserBalance(transactionValidated.sender.copy(balance = transactionValidated.sender.balance - transaction.amount))
        val receiver =
            userService.updateUserBalance(transactionValidated.receiver.copy(balance = transactionValidated.receiver.balance + transaction.amount))
        transactionsRepository.save(transaction)

        launch {
            redisCacheRepository.setKey(
                "transaction::${transaction.senderID}::${transaction.amount}",
                "${transaction.receiverID}",
                5,
                TimeUnit.MINUTES
            )
            notificationSenderRepository.sendNotification(
                listOf(
                    sender,
                    receiver
                )
            )
            logger.info { "Transaction made by ${transaction.senderID} for ${transaction.receiverID} with amount: ${transaction.amount}" }
        }


        return@coroutineScope transaction
    }

}