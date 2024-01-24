//package com.paymentSimple.webservice
//
//import com.paymentSimple.api.UserRequest
//import com.paymentSimple.api.UserResponse
//import com.paymentSimple.api.UserType
//import com.paymentSimple.common.toDomain
//import com.paymentSimple.repositories.UserRepository
//import com.paymentSimple.services.UserService
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.test.web.reactive.server.WebTestClient
//
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class UserControllerTests(
//    @Autowired val webTestClient: WebTestClient,
//    @Autowired val userService: UserService,
//    @Autowired val userController: UserController,
//    @Autowired val userRepository: UserRepository
//
//) {
//
//    @Test
//    fun `should create user successfully`() = runBlocking {
//
//        userRepository.deleteAll()
//        val userRequest = UserRequest(
//            firstName = "Teste",
//            lastName = "Teste",
//            document = "123456789",
//            email = "teste@teste.com",
//            password = "password123",
//            type = UserType.COMMON
//        )
//
//        val result = webTestClient.post()
//            .uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(userRequest)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody(UserResponse::class.java)
//            .consumeWith {
//                assertEquals("COMMON", it.responseBody!!.userType.toString())
//                assertEquals("Teste", it.responseBody!!.firstName)
//                assertEquals("Teste", it.responseBody!!.lastName)
//
//            }
//
//    }
//
//    @Test
//    fun `should throw error when already have user with that document`() = runBlocking {
//        userRepository.deleteAll()
//
//        val userRequest = UserRequest(
//            firstName = "Teste",
//            lastName = "Teste",
//            document = "123456789",
//            email = "teste@teste.com",
//            password = "password123",
//            type = UserType.COMMON
//        )
//        userRepository.save(userRequest.toDomain())
//
//
//        val result = webTestClient.post()
//            .uri("/api/users")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(userRequest)
//            .exchange()
//            .expectStatus().is5xxServerError
//
//    }
//}
