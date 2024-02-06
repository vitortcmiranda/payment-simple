package com.paymentSimple.exceptions

import com.paymentSimple.api.ErrorResponse
import com.paymentSimple.services.TransactionServiceImpl
import mu.KLogger
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvice {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { TransactionServiceImpl::class.java }
    }

    @ExceptionHandler(TransactionNotAllowedException::class)
    fun handleTransactionNotAllowedException(
        ex: TransactionNotAllowedException,
    ): ResponseEntity<ErrorResponse> {
        val erro = ErrorResponse(
            httpCode = HttpStatus.METHOD_NOT_ALLOWED.value(),
            message = "Transaction not allowed for the given user",
            errors = null,
            internalCode = null
        )
        logger.error { ex.message }
        return ResponseEntity(erro, HttpStatus.METHOD_NOT_ALLOWED)
    }
}