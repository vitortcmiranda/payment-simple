package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions

interface TransactionService {
    suspend fun validateTransaction(transaction: Transactions): Boolean
    suspend fun send(transaction: Transactions): Transactions
}