package com.paymentSimple

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentSimpleApplication

fun main(args: Array<String>) {
	runApplication<PaymentSimpleApplication>(*args)
}
