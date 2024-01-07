package com.paymentSimple.webservice

import com.paymentSimple.api.TransactionRequest
import com.paymentSimple.api.TransactionResponse
import com.paymentSimple.common.toModel
import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.services.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/transactions")
class TransactionController(private val transactionService: TransactionService) {
    @PostMapping("")
    suspend fun createTransaction(@RequestBody transaction: TransactionRequest): TransactionResponse {
        return transactionService.send(transaction.toModel()).toModel()
    }
}


