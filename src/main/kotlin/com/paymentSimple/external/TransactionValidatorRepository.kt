package com.paymentSimple.external

import com.paymentSimple.api.TransactionApprovalResponse
import com.paymentSimple.domain.transaction.Transactions

interface TransactionValidatorRepository {
    suspend fun validateTransaction(transactionData: Transactions): TransactionApprovalResponse?
}