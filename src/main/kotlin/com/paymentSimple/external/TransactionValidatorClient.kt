package com.paymentSimple.external

import com.paymentSimple.api.TransactionApprovalResponse
import com.paymentSimple.domain.transaction.Transactions
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient

@Repository
class TransactionValidatorClient : TransactionValidatorRepository {

    private val webClient: WebClient = WebClient.create("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc")

    override suspend fun validateTransaction(transactionData: Transactions): TransactionApprovalResponse? {
        return webClient.get().retrieve().bodyToMono(TransactionApprovalResponse::class.java).awaitSingleOrNull()
    }
}