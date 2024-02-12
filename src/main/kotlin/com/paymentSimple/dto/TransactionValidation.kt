package com.paymentSimple.dto

import com.paymentSimple.domain.user.User
import java.math.BigDecimal

data class TransactionValidation(
    val sender: User,
    val receiver: User,
    val amount: BigDecimal
)
