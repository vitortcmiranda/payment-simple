package com.paymentSimple.api

import java.math.BigDecimal
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val document: String,
    val email: String,
    val balance: BigDecimal,
    val userType: UserType
)
