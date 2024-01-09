package com.paymentSimple.services

import com.paymentSimple.api.MessageApproval
import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.external.NotificationSenderRepository
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.TransactionsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant


@Service
class TransactionServiceImpl(
    private val userService: UserService,
    private val transactionsRepository: TransactionsRepository,
    private val transactionValidatorRepository: TransactionValidatorRepository,
    private val notificationSenderRepository: NotificationSenderRepository
) : TransactionService {
    override suspend fun validateTransaction(transaction: Transactions): List<User> {
        val validations = coroutineScope {
            val receiver = async { userService.findUserById(transaction.receiverID) }
            val sender = async { validateSender(transaction) }
            val approvalResponse =
                async { transactionValidatorRepository.validateTransaction(transaction)!!.message }

            awaitAll(receiver, sender, approvalResponse)
        }

        val externalApproval: Boolean = (validations[2] as MessageApproval) === MessageApproval.Autorizado
        if (!externalApproval) {
            throw ResponseStatusException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Transaction Not Allowed by external approval"
            )
        }


        return listOf(validations[0] as User, validations[1] as User)

    }

    suspend fun validateSender(transaction: Transactions): User {
        val sender = userService.findUserById(transaction.senderID) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not found"
        )
        val isMerchantUser = sender.userType === UserType.MERCHANT
        val hasLessBalanceThanTransaction = sender.balance < transaction.amount
        if (isMerchantUser) {
            throw ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Merchant user not allowed to send money")
        }
        if (hasLessBalanceThanTransaction) {
            throw ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "User doesn't have sufficient fundss")
        }
        return sender

    }

    @Transactional
    override suspend fun send(transaction: Transactions): Transactions = validateTransaction(transaction).let { users ->
        coroutineScope {

            val saveSender = async {
                userService.updateUserBalance(
                    users[0].copy(
                        balance = users[0].balance - transaction.amount,
                        updatedAt = Instant.now()
                    )
                )
            }
            val saveReceiver = async {
                userService.updateUserBalance(
                    users[1].copy(
                        balance = users[1].balance + transaction.amount,
                        updatedAt = Instant.now()
                    )
                )
            }
            val saveTransaction = async { transactionsRepository.save(transaction) }

            val result = listOf(saveSender, saveReceiver, saveTransaction).awaitAll()
            launch { notificationSenderRepository.sendNotification(users) }

            return@coroutineScope result[2] as Transactions
        }
    }

}