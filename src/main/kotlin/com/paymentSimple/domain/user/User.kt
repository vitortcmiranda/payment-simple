package com.paymentSimple.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.*

@Table("users")
data class User(
        @Id val id: UUID? = null,
        val firstName: String,
        val lastName: String,
    //how to make document being unique
        val document: String,
        val email: String,
        val password: String,
        val balance: BigDecimal,
        val userType: UserType
)