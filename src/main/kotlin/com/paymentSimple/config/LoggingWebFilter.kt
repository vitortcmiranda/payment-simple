package com.paymentSimple.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono

@Configuration
class LoggingWebFilter {
    @Bean
    fun requestResponseLoggingFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            val request = exchange.request
            val response = exchange.response

            logRequest(request)

            chain.filter(exchange).then(Mono.fromRunnable {
                logResponse(request, response)
            })
        }
    }

    private fun logRequest(request: ServerHttpRequest) {
        println("Request Method: ${request.method}")
        println("Request Path: ${request.path}")
        println("Request Headers: ${request.headers}")
    }

    private fun logResponse(request: ServerHttpRequest, response: ServerHttpResponse) {
        println("Response Status Code: ${response.statusCode}")
        println("Response Headers: ${response.headers}")

    }
}