package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User
import java.math.BigDecimal

interface TransactionService {
    suspend fun validateTransaction(user: User, transactionAmount: BigDecimal): Boolean
    suspend fun send(transaction: Transactions): Transactions
}