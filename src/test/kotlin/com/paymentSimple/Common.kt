package com.paymentSimple

import com.paymentSimple.api.TransactionRequest
import com.paymentSimple.api.UserRequest
import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class Common {
    companion object {
        fun buildUser(): User =
            User(
                userType = UserType.COMMON,
                id = UUID.randomUUID(),
                email = "teste@email.com",
                balance = BigDecimal.ZERO,
                updatedAt = Instant.now(),
                createdAt = Instant.now(),
                lastName = "lastName",
                firstName = "firstName",
                password = "password",
                document = "123456789"
            )

        fun buildTransaction(): Transactions = Transactions(
            UUID.randomUUID(),
            createdAt = Instant.now(),
            amount = BigDecimal.TEN,
            senderID = UUID.randomUUID(),
            receiverID = UUID.randomUUID(),
            updatedAt = Instant.now()
        )

        fun buildTransactionRequest(): TransactionRequest = TransactionRequest(
            payee = UUID.randomUUID(),
            payer = UUID.randomUUID(),
            value = BigDecimal(20)
        )

        fun buildUserRequest(): UserRequest = UserRequest(
            firstName = "Teste",
            lastName = "Teste",
            document = "123456789",
            email = "teste@teste.com",
            password = "password123",
            type = com.paymentSimple.api.UserType.COMMON
        )
    }
}
