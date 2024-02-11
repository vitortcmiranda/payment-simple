package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.dto.TransactionValidation

interface Validations {

    suspend fun execute(transaction: Transactions): TransactionValidation
}