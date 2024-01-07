package com.paymentSimple.api

import java.math.BigDecimal
import java.time.Instant

data class TransactionResponse(
    val status: Boolean,
    val amount: BigDecimal,
    val createdAt: Instant = Instant.now()
)
