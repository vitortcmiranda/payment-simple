package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User

interface TransactionService {
    suspend fun validateTransaction(transaction: Transactions): List<User>
    suspend fun send(transaction: Transactions): Transactions
}