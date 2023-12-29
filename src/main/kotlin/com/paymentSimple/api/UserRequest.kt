package com.paymentSimple.api

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRequest (
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String,
    val document: String,
    val email: String,
    val password: String,
    val type: UserType
)

enum class UserType {
    COMMON, MERCHANT
}