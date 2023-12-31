package com.paymentSimple.external

import com.paymentSimple.domain.transaction.Transactions

interface TransactionValidatorClient {
    suspend fun validateTransaction(transactionData: Transactions): Transactions
}