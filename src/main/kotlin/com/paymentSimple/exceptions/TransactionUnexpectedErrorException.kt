package com.paymentSimple.exceptions

class TransactionUnexpectedErrorException(override val message: String? = "Unexpected error occurred while executing transaction") :
    Exception() {
}