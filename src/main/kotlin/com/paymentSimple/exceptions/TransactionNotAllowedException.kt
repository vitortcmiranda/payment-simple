package com.paymentSimple.exceptions

import java.lang.Exception

class TransactionNotAllowedException(override val message: String?) : Exception() {
}