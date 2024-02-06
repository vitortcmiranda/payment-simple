package com.paymentSimple.exceptions

class UserAlreadyExists(override val message: String = "User already exists") : Exception() {

}
