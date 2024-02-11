package com.paymentSimple.services

import com.paymentSimple.api.MessageApproval
import com.paymentSimple.domain.transaction.Transactions
import com.paymentSimple.domain.user.User
import com.paymentSimple.domain.user.UserType
import com.paymentSimple.dto.TransactionValidation
import com.paymentSimple.exceptions.TransactionNotAllowedException
import com.paymentSimple.exceptions.UserNotFoundException
import com.paymentSimple.external.TransactionValidatorRepository
import com.paymentSimple.repositories.RedisCacheRepository
import kotlinx.coroutines.coroutineScope
import mu.KLogger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class ValidationsServiceImpl(
    private val userService: UserService,
    private val redisCacheRepository: RedisCacheRepository,
    private val transactionValidatorRepository: TransactionValidatorRepository,


    ) : Validations {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { ValidationsServiceImpl::class.java }
    }

    private suspend fun isValidSender(senderId: UUID, amount: BigDecimal): User {
        val sender = userService.findUserById(senderId) ?: throw UserNotFoundException()
        if (sender.userType === UserType.MERCHANT) {
            throw TransactionNotAllowedException("Merchant user not allowed to send money, id: ${sender.id}")
        }

        val senderHasBalance = sender.balance >= amount
        if (!senderHasBalance) {
            throw TransactionNotAllowedException("User ${sender.id} doesn't have sufficient funds, total amount: ${sender.balance}")
        }
        return sender
    }

    private suspend fun isValidByExternalApproval(transaction: Transactions): Unit {
        val approved =
            transactionValidatorRepository.validateTransaction(transaction)!!.message === MessageApproval.Autorizado

        if (!approved) {
            throw TransactionNotAllowedException("Transaction not allowed by external approval for sender ${transaction.senderID} with amount ${transaction.amount} for destiny ${transaction.receiverID}")
        }
    }

    override suspend fun execute(transaction: Transactions) = coroutineScope {
        val sender = isValidSender(transaction.senderID, transaction.amount)
        isValidByExternalApproval(transaction)
        isDuplicatedTransaction(transaction)
        val receiver = userService.findUserById(transaction.receiverID) ?: throw UserNotFoundException()
        return@coroutineScope TransactionValidation(sender = sender, receiver = receiver, amount = transaction.amount)
    }

    private suspend fun isDuplicatedTransaction(transaction: Transactions): Unit =
        redisCacheRepository.getKey("transaction::${transaction.senderID}::${transaction.amount}").let {
            if (it !== null) {
                throw TransactionNotAllowedException("Operation already made [sender,amount,destination] : [${transaction.senderID},${transaction.amount}, ${transaction.receiverID}]")
            }
        }
}