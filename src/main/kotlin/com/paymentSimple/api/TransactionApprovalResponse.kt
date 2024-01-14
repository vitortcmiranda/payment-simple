package com.paymentSimple.api

data class TransactionApprovalResponse(
    val message: MessageApproval
)

enum class MessageApproval {
    Autorizado, Desautorizado
}
