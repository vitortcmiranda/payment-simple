package com.paymentSimple.services

import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.repositorys.TransactionsRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class TransactionServiceImpl(private val userService: UserService, private val transactionsRepository: TransactionsRepository) : TransactionService {
    override suspend fun validateTransaction(user: User, transactionAmount: BigDecimal): Boolean {
        val isMerchantUser = user.userType === UserType.MERCHANT
        val hasLessBalanceThanTransaction = user.balance < transactionAmount


        if (isMerchantUser) {
            throw Exception("Merchant user not allowed to send money")
        }

        if (hasLessBalanceThanTransaction) {
            throw Exception("User don't have sufficient founds")
        }
        return true
    }

    override suspend fun send(transaction: Transactions): Transactions {
        val sender = userService.findUserById(transaction.senderID)
        sender?: throw Exception("User not found")
        validateTransaction(sender, transaction.amount)
        return transactionsRepository.save(transaction)
    }

}