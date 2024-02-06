package com.paymentSimple.exceptions

class UserNotFoundException(override val message: String = "User not found") : Exception() {

}
