package com.paymentSimple.exceptions

import com.paymentSimple.api.ErrorResponse
import mu.KLogger
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvice {

    companion object {
        private val logger: KLogger = KotlinLogging.logger { com.paymentSimple.exceptions.ControllerAdvice::class.java }
    }

    @ExceptionHandler(TransactionNotAllowedException::class)
    fun handleTransactionNotAllowedException(
        ex: TransactionNotAllowedException,
    ) = ResponseEntity(
        ErrorResponse(
            httpCode = HttpStatus.METHOD_NOT_ALLOWED.value(),
            message = "Transaction not allowed for the given user",
            errors = null,
            internalCode = null
        ), HttpStatus.METHOD_NOT_ALLOWED
    ).also { logger.error { ex.message } }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException) =
        ResponseEntity(
            ErrorResponse(
                httpCode = HttpStatus.NOT_FOUND.value(),
                message = "User not found",
                internalCode = null,
                errors = null
            ), HttpStatus.NOT_FOUND
        ).also { logger.error { ex.message } }


    @ExceptionHandler(UserAlreadyExists::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExists) = ResponseEntity(
        ErrorResponse(
            httpCode = HttpStatus.BAD_REQUEST.value(),
            message = ex.message,
            errors = null,
            internalCode = null
        ), HttpStatus.BAD_REQUEST
    ).also { logger.error { ex.message } }

    @ExceptionHandler(TransactionUnexpectedErrorException::class)
    fun handleTransactionUnexpectedError(ex: TransactionUnexpectedErrorException) =
        ResponseEntity(
            ErrorResponse(
                httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = ex.message!!,
                errors = null,
                internalCode = null
            ), HttpStatus.INTERNAL_SERVER_ERROR
        ).also { logger.error { ex.message } }

}