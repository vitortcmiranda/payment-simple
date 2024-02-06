package com.paymentSimple.api

data class ErrorResponse(
    var httpCode: Int,
    var message: String,
    var internalCode: String?,
    var errors: Any?
)
