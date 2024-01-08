package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.repositories.TransactionsRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException


@Service
class TransactionServiceImpl(
    private val userService: UserService,
    private val transactionsRepository: TransactionsRepository
) : TransactionService {
    override suspend fun validateTransaction(transaction: Transactions): Boolean = coroutineScope {
        userService.findUserById(transaction.receiverID) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Receiver not found"
        )
        validateSender(transaction)
        return@coroutineScope true
    }

    suspend fun validateSender(transaction: Transactions): Boolean = coroutineScope {
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
        return@coroutineScope true

    }

    @Transactional
    override suspend fun send(transaction: Transactions): Transactions =
        validateTransaction(transaction).let { transactionsRepository.save(transaction) }


}