package com.paymentSimple.domain.transaction

import org.springframework.boot.autoconfigure.security.SecurityProperties.User
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("transactions")
data class Transactions(
    @Id val id: Long? = null,
    val amount: BigDecimal,
    //como colocar many to one here
    val sender: User,
    val receiver: User,
    val createdAt: Instant,
    val updatedAt: Instant,
    )
