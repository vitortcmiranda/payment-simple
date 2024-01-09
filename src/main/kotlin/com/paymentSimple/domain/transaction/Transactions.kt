package com.paymentSimple.domain.transaction

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("transactions")
data class Transactions(
    @Id val id: UUID? = null,
    val amount: BigDecimal,
    val senderID: UUID,
    val receiverID: UUID,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
)
