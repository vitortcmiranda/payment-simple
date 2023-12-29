package com.paymentSimple.domain.user

import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("users")
data class User(
    val id: UUID? = null,
    val firstName: String,
    val lastName: String,
    val document: String,
    val email: String,
    val password: String,
    val balance: BigDecimal,
    val userType: UserType,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now()
)