package com.paymentSimple.webservice

import com.paymentSimple.api.UserRequest
import com.paymentSimple.api.UserResponse
import com.paymentSimple.common.toDomain
import com.paymentSimple.common.toResponse
import com.paymentSimple.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("")
    suspend fun createUser(@RequestBody userRequest: UserRequest): UserResponse =
        userService.createUser(userRequest.toDomain()).toResponse()

}
