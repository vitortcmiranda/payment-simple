package com.paymentSimple.api

import java.math.BigDecimal
import java.util.UUID

data class TransactionRequest(
    val value: BigDecimal,
    val payer: UUID,
    val payee: UUID
)
