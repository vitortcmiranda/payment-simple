package com.paymentSimple.common

import com.paymentSimple.api.UserRequest
import com.paymentSimple.api.UserResponse
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import java.math.BigDecimal
import java.time.Instant


fun User.toResponse(): UserResponse? =
    UserResponse(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        document = document,
        email = email,
        balance = balance,
        userType = userType.toDomain()
    )

fun UserType.toDomain(): com.paymentSimple.api.UserType = when (this) {
    UserType.COMMON -> com.paymentSimple.api.UserType.COMMON
    UserType.MERCHANT -> com.paymentSimple.api.UserType.MERCHANT
}

fun UserRequest.toDomain(): User =
    User(
        firstName = firstName,
        lastName = lastName,
        password = password,
        email = email,
        balance = BigDecimal.ZERO,
        userType = type.toDomain(),
        document = document,
        createdAt = Instant.now()
    )

fun com.paymentSimple.api.UserType.toDomain(): UserType = when (this) {
    com.paymentSimple.api.UserType.COMMON -> UserType.COMMON
    com.paymentSimple.api.UserType.MERCHANT -> UserType.MERCHANT
}