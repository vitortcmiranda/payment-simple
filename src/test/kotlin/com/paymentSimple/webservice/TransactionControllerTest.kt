package com.paymentSimple.webservice

import com.paymentSimple.Common.Companion.buildUser
import com.paymentSimple.api.ErrorResponse
import com.paymentSimple.api.TransactionRequest
import com.paymentSimple.api.TransactionResponse
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.repositories.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest(
    @Autowired val webTestClient: WebTestClient,
    @Autowired val userRepository: UserRepository,
) {

    @Test
    fun `should throw error because user is merchant`() = runBlocking {
        val sender =
            userRepository.save(buildUser().copy(id = null, userType = UserType.MERCHANT, balance = BigDecimal(10000)))
        val receiver = userRepository.save(buildUser().copy(id = null))

        val transactionRequest =
            TransactionRequest(value = BigDecimal.TEN, payee = receiver.id!!, payer = sender.id!!)

        val result = webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transactionRequest)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .consumeWith {
                Assertions.assertEquals(405, it.responseBody!!.httpCode)
                Assertions.assertEquals("Transaction not allowed for the given user", it.responseBody!!.message)
            }

    }

    @Test
    fun `should throw error because user does not have sufficient funds`() = runBlocking {
        val sender =
            userRepository.save(buildUser().copy(id = null))
        val receiver = userRepository.save(buildUser().copy(id = null))

        val transactionRequest =
            TransactionRequest(value = BigDecimal.TEN, payee = receiver.id!!, payer = sender.id!!)

        val result = webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transactionRequest)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .consumeWith {
                Assertions.assertEquals(405, it.responseBody!!.httpCode)
                Assertions.assertEquals(
                    "Transaction not allowed for the given user",
                    it.responseBody!!.message
                )
            }

    }

    @Test
    fun `should throw error because user was not found`() = runBlocking {
        val sender =
            userRepository.save(buildUser().copy(id = null))
        val receiver = userRepository.save(buildUser().copy(id = null))

        val transactionRequest =
            TransactionRequest(value = BigDecimal.TEN, payee = UUID.randomUUID(), payer = UUID.randomUUID())

        val result = webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transactionRequest)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(ErrorResponse::class.java)
            .consumeWith {
                Assertions.assertEquals(404, it.responseBody!!.httpCode)
                Assertions.assertEquals(
                    "User not found",
                    it.responseBody!!.message
                )
            }

    }

    @Test
    fun `should successful make a transaction`() = runBlocking {
        val sender =
            userRepository.save(buildUser().copy(id = null, balance = BigDecimal(1000)))
        val receiver = userRepository.save(buildUser().copy(id = null))

        val transactionRequest =
            TransactionRequest(value = BigDecimal.TEN, payee = receiver.id!!, payer = sender.id!!)

        val result = webTestClient.post()
            .uri("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(transactionRequest)
            .exchange()
            .expectStatus().isOk
            .expectBody(TransactionResponse::class.java)
            .consumeWith {
                Assertions.assertEquals(transactionRequest.value, it.responseBody!!.amount)
                Assertions.assertEquals(
                    true,
                    it.responseBody!!.status
                )
            }

    }
}