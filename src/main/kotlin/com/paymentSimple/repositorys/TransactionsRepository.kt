package com.paymentSimple.repositorys

import com.paymentSimple.domain.transaction.Transactions
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface TransactionsRepository : CoroutineCrudRepository<Transactions, UUID> {
}